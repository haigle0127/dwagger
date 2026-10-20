package com.haigle.dwagger.spring.common.insight;

import com.haigle.dwagger.core.util.StrUtil;
import com.haigle.dwagger.insight.InsightConstants;
import com.haigle.dwagger.insight.config.DwaggerInsightCommonInfo;
import com.haigle.dwagger.insight.upload.DwaggerInsightUploadRunner;
import com.haigle.dwagger.spring.configuration.insight.DwaggerInsightProperties;
import com.haigle.dwagger.spring.util.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author haigle
 * @since dwagger v4.4.0
 */
@Slf4j
public class DwaggerInsightDiscoveryBootstrapper implements CommandLineRunner, EnvironmentAware {
    
    final DwaggerInsightProperties insightProperties;
    private Environment environment;
    
    public DwaggerInsightDiscoveryBootstrapper(DwaggerInsightProperties insightProperties) {
        this.insightProperties = insightProperties;
    }
    @Override
    public void run(String... args) throws Exception {
        String serviceName = insightProperties.getServiceName();
        if (StrUtil.isBlank(serviceName)) {
            serviceName = EnvironmentUtils.resolveString(environment, "spring.application.name", "");
        }
        if (StrUtil.isBlank(serviceName)) {
            log.warn("service-name must set one,upload refused.");
            return;
        }
        // 启动时进行异步注册任务
        DwaggerInsightCommonInfo commonInfo = new DwaggerInsightCommonInfo();
        // 属性赋值
        commonInfo.setContextPath(EnvironmentUtils.resolveContextPath(environment));
        commonInfo.setSpec(InsightConstants.SPEC_OPENAPI3);
        commonInfo.setServiceName(serviceName);
        commonInfo.setSecret(insightProperties.getSecret());
        commonInfo.setNamespace(insightProperties.getNamespace());
        commonInfo.setPort(EnvironmentUtils.resolveString(environment, "server.port", "8080"));
        commonInfo.setServer(insightProperties.getServer());
        DwaggerInsightUploadRunner uploadRunner = new DwaggerInsightUploadRunner(commonInfo);
        new Thread(uploadRunner).start();
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
