package com.haigle.dwagger.spring.configuration;

import com.haigle.dwagger.core.conf.GlobalConstants;
import com.haigle.dwagger.extend.filter.basic.AbstractSecurityFilter;
import com.haigle.dwagger.extend.filter.basic.JakartaServletSecurityBasicAuthFilter;
import com.haigle.dwagger.spring.extension.DwaggerJakartaOperationCustomizer;
import com.haigle.dwagger.spring.extension.DwaggerOpenApiCustomizer;
import com.haigle.dwagger.spring.filter.JakartaProductionSecurityFilter;
import com.haigle.dwagger.spring.util.EnvironmentUtils;
import jakarta.servlet.DispatcherType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SpringDocConfigProperties;
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

/***
 * Dwagger 基础自动配置类
 * {@code @since } 2.0.0
 * @author haigle
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties({DwaggerProperties.class, DwaggerSetting.class, DwaggerHttpBasic.class})
@ConditionalOnProperty(name = "dwagger.enable", havingValue = "true")
public class DwaggerAutoConfiguration {
    
    private final DwaggerProperties properties;
    private final Environment environment;
    
    /**
     * 增强自定义配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DwaggerOpenApiCustomizer dwaggerOpenApiCustomizer(SpringDocConfigProperties docProperties) {
        log.debug("Register DwaggerOpenApiCustomizer");
        return new DwaggerOpenApiCustomizer(this.properties, docProperties);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DwaggerJakartaOperationCustomizer dwaggerJakartaOperationCustomizer() {
        return new DwaggerJakartaOperationCustomizer();
    }
    /**
     * 配置Cors
     *
     * @since 2.0.4
     */
    @Bean("dwaggerCorsFilter")
    @ConditionalOnMissingBean(CorsFilter.class)
    @ConditionalOnProperty(name = "dwagger.cors", havingValue = "true")
    public CorsFilter corsFilter() {
        log.info("init CorsFilter...");
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
    @ConditionalOnMissingBean(JakartaServletSecurityBasicAuthFilter.class)
    @ConditionalOnExpression("${dwagger.basic.enable:true}")
    public FilterRegistrationBean<JakartaServletSecurityBasicAuthFilter> securityBasicAuthFilter(DwaggerProperties dwaggerProperties) {
        JakartaServletSecurityBasicAuthFilter authFilter = new JakartaServletSecurityBasicAuthFilter();
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
        FilterRegistrationBean<JakartaServletSecurityBasicAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(authFilter);
        registration.setOrder(AbstractSecurityFilter.SPRING_FILTER_ORDER);
        return registration;
    }
    
    @Bean
    @ConditionalOnMissingBean(JakartaProductionSecurityFilter.class)
    @ConditionalOnProperty(name = "dwagger.production", havingValue = "true")
    public FilterRegistrationBean<JakartaProductionSecurityFilter> productionSecurityFilter(Environment environment) {
        boolean prod = false;
        JakartaProductionSecurityFilter p = null;
        if (properties == null) {
            if (environment != null) {
                String prodStr = environment.getProperty("dwagger.production");
                if (log.isDebugEnabled()) {
                    log.debug("swagger.production:{}", prodStr);
                }
                prod = Boolean.valueOf(prodStr);
            }
            p = new JakartaProductionSecurityFilter(prod);
        } else {
            p = new JakartaProductionSecurityFilter(properties.isProduction());
        }
        FilterRegistrationBean<JakartaProductionSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(p);
        registration.setOrder(AbstractSecurityFilter.SPRING_FILTER_ORDER - 1);
        return registration;
    }
    
}
