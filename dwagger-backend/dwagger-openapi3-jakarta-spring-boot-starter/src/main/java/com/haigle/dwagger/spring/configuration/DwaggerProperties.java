package com.haigle.dwagger.spring.configuration;

import com.haigle.dwagger.core.extend.OpenApiExtendSetting;
import com.haigle.dwagger.core.model.MarkdownProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/***
 * 配置文件
 * {@code @since } 1.9.6
 * @author haigle
 */
@Data
@Component
@ConfigurationProperties(prefix = "dwagger")
public class DwaggerProperties {
    
    /**
     * 是否开启Dwagger增强模式
     */
    private boolean enable = false;
    /**
     * 是否开启默认跨域
     */
    private boolean cors = false;
    
    /**
     * 是否开启BasicHttp验证
     */
    private DwaggerHttpBasic basic;
    
    /**
     * 是否生产环境
     */
    private boolean production = false;
    
    /**
     * 个性化配置
     */
    private DwaggerSetting setting;
    
    /**
     * 分组文档集合
     */
    private List<MarkdownProperty> documents;
    
}
