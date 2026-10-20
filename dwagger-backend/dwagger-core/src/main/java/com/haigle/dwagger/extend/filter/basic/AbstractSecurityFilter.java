package com.haigle.dwagger.extend.filter.basic;

import com.haigle.dwagger.core.util.StrUtil;
import com.haigle.dwagger.extend.filter.BasicFilter;
import lombok.Data;

import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

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
                        // 匹配服务端用户名及密码
                        return Objects.equals(iptUser, getUserName()) && Objects.equals(iptPass, getPassword());
                    }
                }
            }
        }
        return Boolean.TRUE;
    }
}
