package com.haigle.dwagger.annotations;

import java.lang.annotation.*;

/**
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
