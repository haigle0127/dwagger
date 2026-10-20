package com.haigle.dwagger.annotations;

import java.lang.annotation.*;

/**
 * Note: 自Dwagger 4.0版本起，功能保留，但目前阶段放弃维护此属性值，建议开发者建实体类，后续有迭代在更新
 * @since 1.9.5
 * @author haigle
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicResponseParameters {
    
    /***
     * dynamic Model name
     * @return 类名
     */
    String name() default "";
    
    /***
     * list of properties
     * @return 类属性集合
     */
    DynamicParameter[] properties() default @DynamicParameter;
    
}
