package com.haigle.dwagger.core.util;

import java.util.*;

/***
 *
 * @since  2.0.4
 * @author haigle
 */
public abstract class CollectionUtils {
    
    /**
     * 判断集合是否为空
     * @param collection 集合
     * @return 空返回true,非空为false
     */
    public static boolean isEmpty(Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }
    
    public static boolean isNotEmpty(Map<?, ?> map) {
        if (map != null) {
            if (map.size() > 0) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断集合是否非空
     * @param collection 集合
     * @return  非空返回true,空为false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /***
     * 是否为空
     * @param arrs 对象数组
     * @return 空返回true,非空为false
     */
    public static boolean isEmpty(Object... arrs) {
        if (arrs == null || arrs.length == 0) {
            return true;
        }
        return false;
    }
    
    /**
     *  根据数组对象初始化集合
     * @param ts 数组
     * @param <T> 泛型
     * @return 返回ArrayList对象
     */
    public static <T> List<T> newArrayList(T... ts) {
        List<T> list = new ArrayList<>();
        if (ts != null && ts.length > 0) {
            list.addAll(Arrays.asList(ts));
        }
        return list;
    }
    
}
