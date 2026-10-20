package com.haigle.dwagger.core.enums;

/**
 * 排序规则
 * @since dwagger v4.2.0
 * @author haigle 迁移自 dwagger-gateway-spring-boot-starter-4.5.0
 */
public enum GroupOrderStrategy {
    
    /**
     * 默认排序规则，官方swagger-ui默认实现
     */
    alpha,
    /**
     * Dwagger提供的增强排序规则，开发者可扩展x-order，根据数值来自定义排序
     */
    order
}
