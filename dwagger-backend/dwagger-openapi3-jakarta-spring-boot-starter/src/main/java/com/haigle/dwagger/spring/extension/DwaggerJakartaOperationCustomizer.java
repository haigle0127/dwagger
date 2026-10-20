package com.haigle.dwagger.spring.extension;

import com.haigle.dwagger.annotations.ApiOperationSupport;
import com.haigle.dwagger.annotations.ApiSupport;
import com.haigle.dwagger.core.conf.ExtensionsConstants;
import com.haigle.dwagger.core.util.StrUtil;
import com.haigle.dwagger.extend.util.ExtensionUtils;
import io.swagger.v3.oas.models.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * @author haigle
 * @since dwagger
 */
@Slf4j
public class DwaggerJakartaOperationCustomizer implements GlobalOperationCustomizer {
    
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // 解析支持作者、接口排序
        ApiOperationSupport operationSupport = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ApiOperationSupport.class);
        if (operationSupport != null) {
            String author = ExtensionUtils.getAuthors(operationSupport);
            if (StrUtil.isNotBlank(author)) {
                operation.addExtension(ExtensionsConstants.EXTENSION_AUTHOR, author);
            }
            if (operationSupport.order() != 0) {
                operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, operationSupport.order());
            }
        } else {
            // 如果方法级别不存在，再找一次class级别的
            ApiSupport apiSupport = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ApiSupport.class);
            if (apiSupport != null) {
                String author = ExtensionUtils.getAuthor(apiSupport);
                if (StrUtil.isNotBlank(author)) {
                    operation.addExtension(ExtensionsConstants.EXTENSION_AUTHOR, author);
                }
                if (apiSupport.order() != 0) {
                    operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, apiSupport.order());
                }
            }
        }
        
        // 方法重名则进行处理，防止url覆盖
        String className = handlerMethod.getBeanType().getSimpleName();
        String existingId = operation.getOperationId();
        String methodName = handlerMethod.getMethod().getName();
        
        String newId = (StrUtil.isBlank(existingId)) ? className + StrUtil.UNDERLINE + existingId : className + StrUtil.UNDERLINE + methodName;
        operation.setOperationId(newId);
        
        return operation;
    }
}
