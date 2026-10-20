package com.haigle.dwagger.core.util;

/***
 *
 * @since  2.0.4
 * @author haigle
 */
public abstract class Assert {
    
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notBlank(String text, String message) {
        if (text == null || "".equals(text)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isTrue(boolean flag, String message) {
        if (flag == false) {
            throw new IllegalArgumentException(message);
        }
    }
    
}
