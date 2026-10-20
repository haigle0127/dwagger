package com.haigle.dwagger.insight.upload;

import com.haigle.dwagger.insight.config.DwaggerInsightCommonInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author haigle
 * @since dwagger
 */
@Slf4j
@AllArgsConstructor
public class DwaggerInsightUploadRunner implements Runnable {
    
    final DwaggerInsightCommonInfo dwaggerInsightCommonInfo;
    @Override
    public void run() {
        try {
            DwaggerInsightUploader.upload(dwaggerInsightCommonInfo);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }
}
