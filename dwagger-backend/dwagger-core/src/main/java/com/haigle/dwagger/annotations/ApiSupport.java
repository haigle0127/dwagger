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
     * 作者,提供数组形式以展示多个，会和{@link #author()}进行合并展示 <p />
     * issues: <a href="https://gitee.com/xiaoym/dwagger/issues/I6SZMK">Gitee#I6SZMK</a>
     * @since v4.2.0
     * @return 作者
     */
    String[] authors() default {};
    
}
