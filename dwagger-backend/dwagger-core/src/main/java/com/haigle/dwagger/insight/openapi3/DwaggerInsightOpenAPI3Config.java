package com.haigle.dwagger.insight.openapi3;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author haigle
 * @since dwagger
 */
@Getter
@Setter
public class DwaggerInsightOpenAPI3Config {
    
    private String configUrl;
    private String oauth2RedirectUrl;
    private String operationsSorter;
    private boolean showExtensions = true;
    private String tagsSorter;
    private String validatorUrl;
    private List<DwaggerInsightOpenAPI3Route> urls;
}
