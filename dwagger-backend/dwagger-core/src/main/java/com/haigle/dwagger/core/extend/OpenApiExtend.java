package com.haigle.dwagger.core.extend;

import java.util.List;

/**
 * 此类作为Dwagger提供的增强扩展类
 * @author haigle
 * @since  2.0.6
 */
public class OpenApiExtend {
    
    /**
     * Swagger分组名称
     */
    private String groupName;
    /**
     * 扩展配置
     */
    private OpenApiExtendSetting openApiExtendSetting;
    
    /**
     * 扩展其他文档(markdown文件)
     */
    private List<OpenApiExtendMarkdownFile> markdownFiles;
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public OpenApiExtendSetting getOpenApiExtendSetting() {
        return openApiExtendSetting;
    }
    
    public void setOpenApiExtendSetting(OpenApiExtendSetting openApiExtendSetting) {
        this.openApiExtendSetting = openApiExtendSetting;
    }
    
    public List<OpenApiExtendMarkdownFile> getMarkdownFiles() {
        return markdownFiles;
    }
    
    public void setMarkdownFiles(List<OpenApiExtendMarkdownFile> markdownFiles) {
        this.markdownFiles = markdownFiles;
    }
}
