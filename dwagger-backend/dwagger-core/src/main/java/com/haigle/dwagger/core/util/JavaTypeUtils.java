package com.haigle.dwagger.core.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;

/**
 * Java 类型工具，用于把反射类型转成文档可读的简写形式
 *
 * @author haigle
 */
public abstract class JavaTypeUtils {

    /**
     * 下标传参，例如 createTime[0]=a&createTime[1]=b
     */
    public static final String ARRAY_STYLE_INDEXED = "indexed";

    /**
     * 重复参数，例如 createTime=a&createTime=b
     */
    public static final String ARRAY_STYLE_REPEATED = "repeated";

    /**
     * 数组/集合在 query 中的传参方式：List 用下标，数组和其他集合用重复参数
     *
     * @param type Java 类型
     * @return 非数组/集合类型返回 null
     */
    public static String arrayStyle(Type type) {
        Class<?> rawClass = rawClass(type);
        if (rawClass == null) {
            return null;
        }
        if (rawClass.isArray()) {
            return ARRAY_STYLE_REPEATED;
        }
        if (List.class.isAssignableFrom(rawClass)) {
            return ARRAY_STYLE_INDEXED;
        }
        if (Collection.class.isAssignableFrom(rawClass)) {
            return ARRAY_STYLE_REPEATED;
        }
        return null;
    }

    /**
     * 简化类型名，例如 java.util.List&lt;java.lang.String&gt; -> List&lt;String&gt;
     *
     * @param type Java 类型
     * @return 类型简写，type 为 null 时返回 null
     */
    public static String simpleTypeName(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isArray()) {
                return simpleTypeName(clazz.getComponentType()) + "[]";
            }
            return simpleName(clazz);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            StringBuilder builder = new StringBuilder(simpleTypeName(parameterizedType.getRawType()));
            Type[] arguments = parameterizedType.getActualTypeArguments();
            builder.append("<");
            for (int i = 0; i < arguments.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(simpleTypeName(arguments[i]));
            }
            return builder.append(">").toString();
        }
        if (type instanceof GenericArrayType) {
            return simpleTypeName(((GenericArrayType) type).getGenericComponentType()) + "[]";
        }
        if (type instanceof WildcardType) {
            return "?";
        }
        if (type instanceof TypeVariable) {
            return ((TypeVariable<?>) type).getName();
        }
        return type.getTypeName();
    }

    private static Class<?> rawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return rawType instanceof Class ? (Class<?>) rawType : null;
        }
        return null;
    }

    private static String simpleName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return StrUtil.isBlank(simpleName) ? clazz.getName() : simpleName;
    }
}
