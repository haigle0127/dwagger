package com.haigle.dwagger.core.model;

/**
 * @author haigle
 * @since  1.0
 */
public class MarkdownProperty {
    
    /**
     * 分组名称->对应Docket对象中的groupName
     */
    private String group;
    /**
     * 在接口文档中的分组名称,类似于Controller
     */
    private String name;
    /**
     * markdown文件路径
     */
    private String locations;
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocations() {
        return locations;
    }
    
    public void setLocations(String locations) {
        this.locations = locations;
    }
}
