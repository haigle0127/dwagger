package com.haigle.dwagger.spring.util;

import com.haigle.dwagger.core.extend.OpenApiExtendMarkdownChildren;
import com.haigle.dwagger.core.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @since 
 * @author haigle
 */
public class MarkdownUtils {
    
    static final Logger logger = LoggerFactory.getLogger(MarkdownUtils.class);
    
    /**
     * Resolve markdown files
     * @param resource markdown file
     * @return OpenApiExtendMarkdownChildren
     */
    public static OpenApiExtendMarkdownChildren resolveMarkdownResource(Resource resource) {
        try {
            if (resource != null) {
                OpenApiExtendMarkdownChildren markdownFile = new OpenApiExtendMarkdownChildren();
                if (logger.isDebugEnabled()) {
                    logger.debug("read file:" + resource.getFilename());
                }
                // 只读取md
                if (Objects.toString(resource.getFilename(), "").toLowerCase().endsWith(".md")) {
                    // if (".md".equals(Objects.toString(resource.getFilename(),""))){
                    try {
                        String title = CommonUtils.resolveMarkdownTitle(resource.getInputStream(), resource.getFilename());
                        markdownFile.setTitle(title);
                        markdownFile.setContent(new String(CommonUtils.readBytes(resource.getInputStream()), StandardCharsets.UTF_8));
                        return markdownFile;
                    } catch (Exception e) {
                        logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", e.getMessage());
        }
        return null;
    }
}
