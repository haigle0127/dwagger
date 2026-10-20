package com.haigle.dwagger.core.enums;

/**
 * @since  4.0.0
 * @author haigle
 */
public enum OpenAPILanguageEnums {
    
    /**
     * Chinese
     */
    ZH_CN("zh-CN"),
    
    /**
     * English
     */
    EN("en-US");
    
    private String value;
    OpenAPILanguageEnums(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    public String getValue() {
        return value;
    }
}
