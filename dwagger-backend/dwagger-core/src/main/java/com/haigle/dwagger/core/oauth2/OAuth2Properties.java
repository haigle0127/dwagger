package com.haigle.dwagger.core.oauth2;

import com.haigle.dwagger.core.enums.OAuth2TypeEnums;
import lombok.Data;

import java.util.List;

/**
 * @author haigle
 * @since 
 */
@Data
public class OAuth2Properties {
    
    /**
     * OAuth2 type
     */
    private OAuth2TypeEnums grantType;
    
    /**
     * OAuth2 config properties
     */
    private OAuth2Info config;
    
    /**
     * OAuth2 Scope
     */
    private List<OAuth2Scope> scopes;
    
    /**
     * OAuth2 APi Path Collection,Only support Ant,not support regex.
     * example: /api/**, /config/** etc.
     */
    private List<String> paths;
}
