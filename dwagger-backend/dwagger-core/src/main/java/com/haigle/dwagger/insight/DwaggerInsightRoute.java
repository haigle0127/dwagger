package com.haigle.dwagger.insight;

import lombok.Getter;
import lombok.Setter;

/**
 * @author haigle
 * @since dwagger v4.4.0
 */
@Getter
@Setter
public class DwaggerInsightRoute {
    
    /**
     * 分组名称
     */
    private String groupName;
    
    /**
     * JSON内容
     */
    private String content;
    /**
     * 路径
     */
    private String path;
}
