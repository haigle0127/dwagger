package com.haigle.dwagger.core.model;

/***
 *
 * @since  1.0
 * @author haigle
 */
public class DynamicClass {
    
    private Class<?> targetClazz;
    
    private Class<?> fieldClazz;
    
    public Class<?> getTargetClazz() {
        return targetClazz;
    }
    
    public void setTargetClazz(Class<?> targetClazz) {
        this.targetClazz = targetClazz;
    }
    
    public Class<?> getFieldClazz() {
        return fieldClazz;
    }
    
    public void setFieldClazz(Class<?> fieldClazz) {
        this.fieldClazz = fieldClazz;
    }
    
}
