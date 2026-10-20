package com.haigle.dwagger.insight;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haigle
 * @since dwagger v4.4.0
 */
@Getter
@Setter
public class DwaggerInsightDiscoveryInfo {
    
    /**
     * 账号
     */
    private String accessKey;
    
    /**
     * 项目namespace
     */
    private String namespace;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 规范类别，OpenAPI3或者Swagger2
     */
    private String spec;
    
    /**
     * 当前上传的应用服务ip
     */
    private String host;
    
    /**
     * 当前应用端口号
     */
    private String port;
    
    /**
     * 分组实例
     */
    private List<DwaggerInsightRoute> cloudRoutes = new ArrayList<>();
    
    public void addRoute(DwaggerInsightRoute route) {
        cloudRoutes.add(route);
    }
}
