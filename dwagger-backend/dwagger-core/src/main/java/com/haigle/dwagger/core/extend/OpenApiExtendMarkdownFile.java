package com.haigle.dwagger.core.extend;

import lombok.Data;

import java.util.List;

/**
 * @author haigle
 * @since  2.0.6
 */
@Data
public class OpenApiExtendMarkdownFile {
    
    /**
     * OpenAPI分组名称
     * since v4.0.0
     */
    private String group;
    
    /**
     * markdown分组名称
     */
    private String name;
    
    /**
     * 分组下文档集合
     */
    private List<OpenApiExtendMarkdownChildren> children;
    
}
