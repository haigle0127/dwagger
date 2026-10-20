package com.haigle.dwagger.spring.model;

import com.haigle.dwagger.core.extend.OpenApiExtendMarkdownChildren;

import java.util.ArrayList;
import java.util.List;

/***
 * <p>
 * {@code @since } 1.8.5
 * @author haigle
 */
public class SwaggerBootstrapUi {
    
    /***
     * tag排序属性
     */
    protected List<SwaggerBootstrapUiTag> tagSortLists = new ArrayList<>();
    
    /***
     * path排序
     */
    private List<SwaggerBootstrapUiPath> pathSortLists = new ArrayList<>();
    
    /***
     * 自定义菜单内容
     */
    private List<OpenApiExtendMarkdownChildren> markdownFiles = new ArrayList<>();
    
    /***
     * 开启增强失败,错误原因
     */
    private String errorMsg;
    
    public List<SwaggerBootstrapUiPath> getPathSortLists() {
        return pathSortLists;
    }
    
    public void setPathSortLists(List<SwaggerBootstrapUiPath> pathSortLists) {
        this.pathSortLists = pathSortLists;
    }
    
    public List<SwaggerBootstrapUiTag> getTagSortLists() {
        return tagSortLists;
    }
    
    public void setTagSortLists(List<SwaggerBootstrapUiTag> tagSortLists) {
        this.tagSortLists = tagSortLists;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public List<OpenApiExtendMarkdownChildren> getMarkdownFiles() {
        return markdownFiles;
    }
    
    public void setMarkdownFiles(List<OpenApiExtendMarkdownChildren> markdownFiles) {
        this.markdownFiles = markdownFiles;
    }
}
