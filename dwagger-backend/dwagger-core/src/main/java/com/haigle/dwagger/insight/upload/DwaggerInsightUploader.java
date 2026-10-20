package com.haigle.dwagger.insight.upload;

import cn.hutool.json.JSONUtil;
import com.haigle.dwagger.core.util.DwaggerUtils;
import com.haigle.dwagger.core.util.StrUtil;
import com.haigle.dwagger.insight.DwaggerInsightDiscoveryInfo;
import com.haigle.dwagger.insight.DwaggerInsightRoute;
import com.haigle.dwagger.insight.config.DwaggerInsightCommonInfo;
import com.haigle.dwagger.insight.openapi3.DwaggerInsightOpenAPI3Config;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.haigle.dwagger.insight.InsightConstants.DWAGGER_CLOUD_API;

/**
 * @author haigle
 * @since dwagger v4.4.0
 */
@Slf4j
public class DwaggerInsightUploader {
    
    /**
     * 上传OpenAPI3规范
     *
     * @param commonInfo 配置信息
     */
    public static void upload(DwaggerInsightCommonInfo commonInfo) throws UnknownHostException {
        DwaggerInsightDiscoveryInfo dwaggerCloudDiscoveryInfo = new DwaggerInsightDiscoveryInfo();
        // spec
        dwaggerCloudDiscoveryInfo.setSpec(commonInfo.getSpec());
        // 端口+服务地址
        dwaggerCloudDiscoveryInfo.setHost(InetAddress.getLocalHost().getHostAddress());
        dwaggerCloudDiscoveryInfo.setPort(commonInfo.getPort());
        dwaggerCloudDiscoveryInfo.setServiceName(commonInfo.getServiceName());
        dwaggerCloudDiscoveryInfo.setAccessKey(commonInfo.getSecret());
        dwaggerCloudDiscoveryInfo.setNamespace(commonInfo.getNamespace());
        // openapi3规范中是需要访问url初始化访问的，本地直接访问一次，获取数据后在注册到Insight中
        String localGroup = "http://localhost:" + commonInfo.getPort() + commonInfo.getContextPath() + "/v3/api-docs/swagger-config";
        log.debug("localHost:{}", localGroup);
        String response = DwaggerUtils.getRetry(localGroup, 3);
        if (StrUtil.isNotBlank(response)) {
            DwaggerInsightOpenAPI3Config openAPI3Config = JSONUtil.toBean(response, DwaggerInsightOpenAPI3Config.class);
            if (openAPI3Config.getUrls() != null && !openAPI3Config.getUrls().isEmpty()) {
                openAPI3Config.getUrls().forEach(route -> {
                    // 不为空，获取分组信息
                    DwaggerInsightRoute dwaggerCloudRoute = new DwaggerInsightRoute();
                    dwaggerCloudRoute.setPath(route.getUrl());
                    dwaggerCloudRoute.setGroupName(route.getName());
                    String apiUrl = "http://localhost:" + commonInfo.getPort() + route.getUrl();
                    log.debug("apiUrl:{}", apiUrl);
                    dwaggerCloudRoute.setContent(DwaggerUtils.getRetry(apiUrl, 3));
                    dwaggerCloudDiscoveryInfo.addRoute(dwaggerCloudRoute);
                });
            }
        }
        String cloudApi = commonInfo.getServer() + DWAGGER_CLOUD_API;
        log.debug("Start Register To DwaggerInsight ,CloudApi:{}", cloudApi);
        String body = JSONUtil.toJsonStr(dwaggerCloudDiscoveryInfo);
        // upload
        String result = DwaggerUtils.postRetry(cloudApi, body, 3);
        if (result != null) {
            log.debug("Register To DwaggerInsight Finished");
        }
    }
}
