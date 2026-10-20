package com.haigle.dwagger.spring.configuration.insight;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参考接口文档：<a href="http://dwagger.net/api.html">http://dwagger.net/api.html</a>
 * @author haigle
 * @since dwagger 4.4.0
 */
@Data
@ConfigurationProperties(prefix = "dwagger.insight")
public class DwaggerInsightProperties {
    
    /**
     * 是否启动DwaggerInsight自动上报功能
     */
    private boolean enable = false;
    /**
     * 上报DwaggerInsight数据源地址，可以使用在线版本，例如：<a href="http://console.dwagger.net">http://console.dwagger.net</a>
     */
    private String server;
    /**
     * 上传用户凭证密钥，在DwaggerInsight用户中心获取
     */
    private String secret;
    
    /**
     * 上传namespace,如果不指定，DwaggerInsight会默认根据当前应用的application-name生成，如果应用名称为空,那么会随机生成一个
     */
    private String namespace;
    
    /**
     * 上传服务名称，配置后使用该名称，如果未配置，默认再读取spring.application.name名称，如果都未空，无法上传
     */
    private String serviceName;
}
