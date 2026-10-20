package com.haigle.dwagger.spring.model;

import com.haigle.dwagger.core.extend.OpenApiExtendMarkdownChildren;

import java.util.List;

/**
 * @author haigle
 * {@code @since } 1.0
 */
public class MarkdownFolder {
    
    /**
     * 目录名称
     */
    private String directory;
    /**
     * 目录通配符,作用于去读该directory目录下的md文件
     */
    private String locations;
    
    /**
     * md文件
     */
    private List<OpenApiExtendMarkdownChildren> markdownFiles;
    
    public String getDirectory() {
        return directory;
    }
    
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    
    public String getLocations() {
        return locations;
    }
    
    public void setLocations(String locations) {
        this.locations = locations;
    }
    
    public List<OpenApiExtendMarkdownChildren> getMarkdownFiles() {
        return markdownFiles;
    }
    
    public void setMarkdownFiles(List<OpenApiExtendMarkdownChildren> markdownFiles) {
        this.markdownFiles = markdownFiles;
    }
}
