package com.haigle.dwagger.core.conf;

/***
 *
 * @since  1.9.0
 * @author haigle
 */
public class GlobalConstants {
    
    /***
     * basic auth验证
     */
    public static final String DWAGGER_BASIC_AUTH_SESSION = "DWAGGER_BASIC_AUTH_SESSION";
    
    /**
     * 校验Basic请求头
     */
    public static final String AUTH_HEADER_NAME = "Authorization";
    
    /**
     * HTTP Schema
     */
    public static final String PROTOCOL_HTTP = "http://";
    
    /**
     * HTTPS Schema
     */
    public static final String PROTOCOL_HTTPS = "https://";
    
    /**
     * 空字符串
     */
    public static final String EMPTY_STR = "";
    
    /**
     * Dwagger provider default username with basic auth.
     */
    public static final String BASIC_DEFAULT_USERNAME = "admin";
    
    /**
     * Dwagger provider default password with basic auth.
     */
    public static final String BASIC_DEFAULT_PASSWORD = "123321";
    
    /**
     * Cors max_age
     */
    public static final Long CORS_MAX_AGE = 10000L;
    
    /**
     * 动态生成class包名前缀
     */
    public static final String BASE_PACKAGE_PREFIX = "com.haigle.dwagger.model.";
    
    /**
     * Dwagger增强扩展属性名称
     */
    public static final String EXTENSION_NAME = "extensions";
    
    /**
     * Dwagger增强扩展属性名称
     */
    public static final String EXTENSION_OPEN_API_NAME = "x-openapi";
    
    /**
     * Dwagger增强扩展配置属性
     */
    public static final String EXTENSION_OPEN_SETTING_NAME = "x-setting";
    
    /**
     * Dwagger增强扩展配置属性
     */
    public static final String EXTENSION_OPEN_MARKDOWN_NAME = "x-markdownFiles";
    
    /**
     * 响应HTTP状态码
     */
    public static final Integer BASIC_SECURITY_RESPONSE_CODE = 401;
    
    /**
     * 生产环境屏蔽后自定义响应HTTP状态码
     */
    public static final Integer FORBIDDEN_CUSTOM_CODE = 200;
}
