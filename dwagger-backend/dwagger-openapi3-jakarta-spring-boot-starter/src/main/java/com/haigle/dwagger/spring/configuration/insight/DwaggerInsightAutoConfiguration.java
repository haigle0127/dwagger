package com.haigle.dwagger.spring.configuration.insight;

import com.haigle.dwagger.spring.insight.DwaggerInsightDiscoveryBootstrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DwaggerInsight产品自动注册，上报OpenAPI/Swagger2数据源
 * @author haigle
 * @since dwagger v4.4.0
 */
@Configuration
@EnableConfigurationProperties(value = {DwaggerInsightProperties.class})
@ConditionalOnProperty(name = "dwagger.insight.enable", havingValue = "true")
public class DwaggerInsightAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public DwaggerInsightDiscoveryBootstrapper dwaggerInsightDiscoveryBootstrapper(DwaggerInsightProperties insightProperties) {
        return new DwaggerInsightDiscoveryBootstrapper(insightProperties);
    }
}
