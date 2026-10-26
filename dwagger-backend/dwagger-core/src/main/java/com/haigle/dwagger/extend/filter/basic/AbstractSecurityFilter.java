package com.haigle.dwagger.extend.filter.basic;

import com.haigle.dwagger.core.util.StrUtil;
import com.haigle.dwagger.extend.filter.BasicFilter;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;

/**
 * @author haigle
 * @since dwagger
 */
@Data
public abstract class AbstractSecurityFilter extends BasicFilter {
    
    /**
     * Spring过滤器顺序
     */
    public static final int SPRING_FILTER_ORDER = Integer.MAX_VALUE;
    
    /***
     * 是否开启basic验证,默认不开启
     */
    private boolean enableBasicAuth = false;
    
    private String userName;
    
    private String password;
    
    /**
     * 外部自定义包含过滤地址，避免BasicFilter父类中不满足的情况，交给开发者自定义传入
     */
    private List<String> include;
    
    /**
     * // SpringMVC环境中,由此init方法初始化此Filter,SpringBoot环境中则不同
     * @param enumeration
     * @param initBasicEnable
     * @param initUserName
     * @param initPassword
     */
    protected void initServletConfig(Enumeration<String> enumeration, String initBasicEnable, String initUserName, String initPassword) {
        if (enumeration != null && enumeration.hasMoreElements()) {
            setEnableBasicAuth(Boolean.valueOf(initBasicEnable));
            setUserName(initUserName);
            setPassword(initPassword);
        }
    }
    
    /**
     * Basic验证
     * @param url 当前系统访问URL
     * @param sessionAuth Basic验证服务器SessionObject
     * @param auth 请求头value
     * @return
     */
    protected boolean tryCommonBasic(String url, Object sessionAuth, String auth) {
        if (this.isEnableBasicAuth()) {
            if (this.match(url)) {
                if (sessionAuth == null) {
                    // 获取请求头Authorization
                    if (StrUtil.isBlank(auth)) {
                        return false;
                    }
                    if (auth.length() < 6) {
                        return false;
                    }
                    String userAndPass = decodeBase64(auth.substring(6));
                    if (userAndPass == null) {
                        return false;
                    }
                    String[] upArr = userAndPass.split(":");
                    if (upArr.length != 2) {
                        return false;
                    } else {
                        String iptUser = upArr[0];
                        String iptPass = upArr[1];
                        // 使用常量时间比较，防止通过响应时间差异爆破用户名/密码
                        boolean userMatch = MessageDigest.isEqual(
                                (iptUser != null ? iptUser : "").getBytes(StandardCharsets.UTF_8),
                                (getUserName() != null ? getUserName() : "").getBytes(StandardCharsets.UTF_8));
                        boolean passMatch = MessageDigest.isEqual(
                                (iptPass != null ? iptPass : "").getBytes(StandardCharsets.UTF_8),
                                (getPassword() != null ? getPassword() : "").getBytes(StandardCharsets.UTF_8));
                        return userMatch && passMatch;
                    }
                }
            }
        }
        return Boolean.TRUE;
    }
}
