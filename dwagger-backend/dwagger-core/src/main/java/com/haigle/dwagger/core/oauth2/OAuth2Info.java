package com.haigle.dwagger.core.oauth2;

import lombok.Data;

/**
 * @since 
 * @author haigle
 */
@Data
public class OAuth2Info {
    
    /**
     * token URL
     */
    private String url;
    
    /**
     * token name
     */
    private String tokenName;
    
    /**
     * OAuth2 authorize URL,if grantType is authorization_code,you must config this property.
     */
    private String authorize;
    
    /**
     * OAuth2 clientId,if grantType is authorization_code,you must config this property.
     */
    private String clientId;
    
    /**
     * OAuth2 clientSecret,if grantType is authorization_code,you must config this property.
     */
    private String clientSecret;
}
