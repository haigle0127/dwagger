package com.haigle.dwagger.annotations;

import java.lang.annotation.*;

/**
 * <p>Help Java development engineers build powerful Swagger documents</p>
 * <p>This annotation belongs to the enhanced annotation of @Api, which is unique to Dwagger and provides Swagger's extended attributes.</p>
 * @since 2.0.3
 * @author haigle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiSupport {
    
    /***
     * Sort Fields
     * @return 排序
     */
    int order() default Integer.MAX_VALUE;
    
    /***
     * author
     * @return 开发者
     */
    String author() default "";
    
    /**
     * authors
     * @return 作者
     */
    String[] authors() default {};
    
}
