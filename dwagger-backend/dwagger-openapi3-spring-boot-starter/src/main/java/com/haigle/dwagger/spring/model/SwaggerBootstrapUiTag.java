package com.haigle.dwagger.spring.model;

import io.swagger.v3.oas.models.tags.Tag;

/***
 *
 * @since  1.8.5
 * @author haigle
 */
public class SwaggerBootstrapUiTag extends Tag {
    
    /**
     * add at 2020-3-31 13:03:39 by xiaoymin
     */
    private String author;
    
    private Integer order;
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Integer getOrder() {
        return order;
    }
    
    public void setOrder(Integer order) {
        this.order = order;
    }
    
    public SwaggerBootstrapUiTag() {
    }
    
    public SwaggerBootstrapUiTag(Integer order) {
        this.order = order;
    }
}
