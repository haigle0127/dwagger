package com.haigle.dwagger.annotations;

import java.lang.annotation.*;

/**
 * Note: 自Dwagger 4.0版本起，功能保留，但目前阶段放弃维护此属性值，建议开发者建实体类，后续有迭代在更新
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicParameter {
    
    /**
     * Name of the parameter.
     * @return 属性名称
     */
    String name() default "";
    
    /**
     * A brief description of the parameter.
     * @return 属性说明
     */
    String value() default "";
    
    /**
     * Specifies if the parameter is required or not.
     * <p>
     * Path parameters should always be set as required.
     * @return 是否必传
     */
    boolean required() default false;
    
    /**
     * The class of the parameter.
     * <p>
     * Overrides {@code dataType} if provided.
     * @return 属性类型
     */
    Class<?> dataTypeClass() default Void.class;
    
    /**
     * a single example for non-body type parameters
     *
     * @since 1.5.4
     *
     * @return 属性示例
     */
    String example() default "";
    
}
