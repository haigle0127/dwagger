package com.haigle.dwagger.spring.extension;

import com.haigle.dwagger.core.conf.ExtensionsConstants;
import com.haigle.dwagger.core.util.JavaTypeUtils;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.PropertyCustomizer;

import java.lang.reflect.Type;

/**
 * 给数组/集合属性补充 Java 类型与传参方式。
 * OpenAPI 无法区分 List&lt;String&gt; 和 String[]，前端需要靠这两个扩展生成不同的请求。
 *
 * @author haigle
 * @since dwagger
 */
public class DwaggerPropertyCustomizer implements PropertyCustomizer {
    
    @Override
    public Schema customize(Schema property, AnnotatedType annotatedType) {
        Type type = annotatedType.getType();
        String arrayStyle = JavaTypeUtils.arrayStyle(type);
        if (arrayStyle == null) {
            return property;
        }
        property.addExtension(ExtensionsConstants.EXTENSION_JAVA_TYPE, JavaTypeUtils.simpleTypeName(type));
        property.addExtension(ExtensionsConstants.EXTENSION_JAVA_ARRAY_STYLE, arrayStyle);
        return property;
    }
}
