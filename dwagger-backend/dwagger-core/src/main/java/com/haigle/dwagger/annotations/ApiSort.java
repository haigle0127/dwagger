package com.haigle.dwagger.annotations;

import java.lang.annotation.*;

/**
 * Deprecated since 2.0.3,see {@link ApiSupport}
 * @since 1.8.5
 * @author haigle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiSort {
    
    /**
     * tag order value, Deprecated since 2.0.3,see {@link ApiSupport} order field
     * @return order
     */
    int value() default Integer.MAX_VALUE;
}
