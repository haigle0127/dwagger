package com.haigle.dwagger.spring.configuration;

import com.haigle.dwagger.core.conf.GlobalConstants;
import com.haigle.dwagger.extend.filter.basic.AbstractSecurityFilter;
import com.haigle.dwagger.extend.filter.basic.ServletSecurityBasicAuthFilter;
import com.haigle.dwagger.spring.extension.DwaggerOpenApiCustomizer;
import com.haigle.dwagger.spring.extension.DwaggerOperationCustomizer;
import com.haigle.dwagger.spring.filter.ProductionSecurityFilter;
import com.haigle.dwagger.spring.util.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.DispatcherType;

/***
 * Dwagger 基础自动配置类
 * @since  2.0.0
 * @author haigle
 */
@Configuration
@EnableConfigurationProperties({DwaggerProperties.class, DwaggerHttpBasic.class, DwaggerSetting.class})
@ConditionalOnProperty(name = "dwagger.enable", havingValue = "true")
public class DwaggerAutoConfiguration {
    
    @Autowired
    private Environment environment;
    Logger logger = LoggerFactory.getLogger(DwaggerAutoConfiguration.class);
    
    /**
     * 增强自定义配置
     * @param dwaggerProperties
     * @param docProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DwaggerOpenApiCustomizer dwaggerOpenApiCustomizer(DwaggerProperties dwaggerProperties,
                                                             SpringDocConfigProperties docProperties) {
        logger.debug("Register DwaggerOpenApiCustomizer");
        return new DwaggerOpenApiCustomizer(dwaggerProperties, docProperties);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DwaggerOperationCustomizer dwaggerOperationCustomizer() {
        return new DwaggerOperationCustomizer();
    }
    /**
     * 配置Cors
     * @since 2.0.4
     * @return
     */
    @Bean("dwaggerCorsFilter")
    @ConditionalOnMissingBean(CorsFilter.class)
    @ConditionalOnProperty(name = "dwagger.cors", havingValue = "true")
    public CorsFilter corsFilter() {
        logger.info("init CorsFilter...");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(10000L);
        // 匹配所有API
        source.registerCorsConfiguration("/**", corsConfiguration);
        CorsFilter corsFilter = new CorsFilter(source);
        return corsFilter;
    }
    
    /**
     * Security with Basic Http
     * @param dwaggerProperties Basic Properties
     * @return BasicAuthFilter
     */
    @Bean
    @ConditionalOnMissingBean(ServletSecurityBasicAuthFilter.class)
    @ConditionalOnExpression("${dwagger.production:false} && ${dwagger.basic.enable:true}")
    public FilterRegistrationBean<ServletSecurityBasicAuthFilter> securityBasicAuthFilter(DwaggerProperties dwaggerProperties) {
        ServletSecurityBasicAuthFilter authFilter = new ServletSecurityBasicAuthFilter();
        if (dwaggerProperties == null) {
            authFilter.setEnableBasicAuth(EnvironmentUtils.resolveBool(environment, "dwagger.basic.enable", Boolean.FALSE));
            authFilter.setUserName(EnvironmentUtils.resolveString(environment, "dwagger.basic.username", GlobalConstants.BASIC_DEFAULT_USERNAME));
            authFilter.setPassword(EnvironmentUtils.resolveString(environment, "dwagger.basic.password", GlobalConstants.BASIC_DEFAULT_PASSWORD));
        } else {
            // 判断非空
            if (dwaggerProperties.getBasic() == null) {
                authFilter.setEnableBasicAuth(Boolean.FALSE);
                authFilter.setUserName(GlobalConstants.BASIC_DEFAULT_USERNAME);
                authFilter.setPassword(GlobalConstants.BASIC_DEFAULT_PASSWORD);
            } else {
                authFilter.setEnableBasicAuth(dwaggerProperties.getBasic().isEnable());
                authFilter.setUserName(dwaggerProperties.getBasic().getUsername());
                authFilter.setPassword(dwaggerProperties.getBasic().getPassword());
                authFilter.addRule(dwaggerProperties.getBasic().getInclude());
            }
        }
        FilterRegistrationBean<ServletSecurityBasicAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(authFilter);
        registration.setOrder(AbstractSecurityFilter.SPRING_FILTER_ORDER);
        return registration;
    }
    
    @Bean
    @ConditionalOnMissingBean(ProductionSecurityFilter.class)
    @ConditionalOnProperty(name = "dwagger.production", havingValue = "true")
    public FilterRegistrationBean<ProductionSecurityFilter> productionSecurityFilter(DwaggerProperties dwaggerProperties) {
        boolean prod = false;
        ProductionSecurityFilter p = null;
        if (dwaggerProperties == null) {
            if (environment != null) {
                String prodStr = environment.getProperty("dwagger.production");
                if (logger.isDebugEnabled()) {
                    logger.debug("swagger.production:{}", prodStr);
                }
                prod = Boolean.valueOf(prodStr);
            }
            p = new ProductionSecurityFilter(prod);
        } else {
            p = new ProductionSecurityFilter(dwaggerProperties.isProduction());
        }
        FilterRegistrationBean<ProductionSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(p);
        registration.setOrder(AbstractSecurityFilter.SPRING_FILTER_ORDER - 1);
        return registration;
    }
    
}
