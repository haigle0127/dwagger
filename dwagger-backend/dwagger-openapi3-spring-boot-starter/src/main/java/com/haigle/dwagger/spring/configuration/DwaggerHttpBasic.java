package com.haigle.dwagger.spring.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/***
 * 配置文件
 * @since  1.9.6
 * @author haigle
 */
@Data
@ConfigurationProperties(prefix = "dwagger.basic")
public class DwaggerHttpBasic {
    
    /**
     * basick是否开启,默认为false
     */
    private boolean enable = false;
    
    /**
     * basic 用户名
     */
    private String username;
    
    /**
     * basic 密码
     */
    private String password;
    
    /**
     * All configured urls need to be verified by basic，Only support Regex
     * since 4.1.0
     */
    private List<String> include;
    
}
