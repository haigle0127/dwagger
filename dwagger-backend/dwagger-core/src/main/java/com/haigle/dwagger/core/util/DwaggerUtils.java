package com.haigle.dwagger.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author haigle
 * @since dwagger v4.4.0
 */
@Slf4j
public class DwaggerUtils {

    // 禁止的协议，防止通过 file://、jar:// 等非 HTTP 协议读取本地或类路径资源
    private static final Set<String> ALLOWED_SCHEMES = new HashSet<>(Arrays.asList("http", "https"));
    // 常见的内部/元数据服务地址，防止 SSRF 探测
    private static final Set<String> FORBIDDEN_HOSTS = new HashSet<>(Arrays.asList(
            "169.254.169.254",
            "metadata.google.internal",
            "metadata.azure.internal",
            "metadata.aws.internal"
    ));

    public static void main(String[] args) {
        System.out.println(GetUrl("http://localhost:17812/v3/api-docs/用户模块"));
    }
    
    public static String GetUrl(String url) {
        // 提取中文字符
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            String chinese = matcher.group();
            try {
                // 进行URL编码
                String encodedChinese = URLEncoder.encode(chinese, StandardCharsets.UTF_8.name());
                // 替换原始URL中的中文字符
                url = url.replace(chinese, encodedChinese);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return url;
    }
    
    /**
     * 重试次数
     * @param url url
     * @param body 参数
     * @param retry 重试次数
     * @return result
     */
    public static String postRetry(String url, String body, int retry) {
        for (int i = 0; i < retry; i++) {
            String result = post(url, body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    public static String getRetry(String url, int retry) {
        for (int i = 0; i < retry; i++) {
            String result = get(url);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    public static String get(String url) {
        URL apiUrl = null;
        try {
            String realUrl = GetUrl(url);
            log.debug("url:{}", realUrl);
            apiUrl = new URL(realUrl);
            if (!isSafeUrl(apiUrl)) {
                log.warn("拒绝不安全的出站请求: {}", realUrl);
                return null;
            }
            HttpURLConnection connection = getGetUrlConnection(apiUrl);
            String response = getEntity(connection);
            if (response != null)
                return response;
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 校验 URL 是否允许访问，缓解 SSRF 风险
     */
    private static boolean isSafeUrl(URL url) {
        if (url == null) {
            return false;
        }
        String protocol = url.getProtocol();
        if (protocol == null || !ALLOWED_SCHEMES.contains(protocol.toLowerCase(Locale.ROOT))) {
            return false;
        }
        String host = url.getHost();
        if (host == null || host.isEmpty()) {
            return false;
        }
        String lowerHost = host.toLowerCase(Locale.ROOT);
        if (FORBIDDEN_HOSTS.contains(lowerHost)) {
            return false;
        }
        // 阻止裸 IPv4 元数据地址（如 169.254.169.254 及其变体已在集合中）
        try {
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();
            if (FORBIDDEN_HOSTS.contains(ip)) {
                return false;
            }
        } catch (Exception e) {
            // 域名解析失败时保守放行，后续连接会失败
        }
        return true;
    }
    
    private static String getEntity(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            CommonUtils.close(in);
            return response.toString();
        }
        return null;
    }
    
    /**
     * http post
     * @param url http url
     * @param body body
     * @return result
     */
    public static String post(String url, String body) {
        URL apiUrl = null;
        try {
            String realUrl = GetUrl(url);
            log.debug("url:{}", realUrl);
            log.debug("body:{}", body);
            apiUrl = new URL(realUrl);
            if (!isSafeUrl(apiUrl)) {
                log.warn("拒绝不安全的出站请求: {}", realUrl);
                return null;
            }
            HttpURLConnection connection = getUrlConnection(apiUrl);
            // 发送数据
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes(StandardCharsets.UTF_8));
            CommonUtils.close(os);
            String response = getEntity(connection);
            if (response != null)
                return response;
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
        return null;
    }
    
    private static HttpURLConnection getUrlConnection(URL apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        // 基础属性
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        // 默认JSON
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        setConnection(connection);
        return connection;
    }
    
    private static HttpURLConnection getGetUrlConnection(URL apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        // 基础属性
        connection.setRequestMethod("GET");
        // 设置通用的请求属性
        setConnection(connection);
        return connection;
    }
    
    private static void setConnection(HttpURLConnection connection) {
        // 设置通用的请求属性
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        // 默认JSON
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 禁止自动跟随重定向，防止 SSRF 通过 30x 跳转绕过目标校验
        connection.setInstanceFollowRedirects(false);
        // 设置请求链接超时时间为20000毫秒（20秒）
        // 设置读取超时时间
        connection.setReadTimeout(20000);
        connection.setConnectTimeout(20000);

    }
}
