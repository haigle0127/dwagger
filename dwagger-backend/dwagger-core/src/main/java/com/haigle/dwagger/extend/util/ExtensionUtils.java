package com.haigle.dwagger.extend.util;

import com.haigle.dwagger.annotations.ApiOperationSupport;
import com.haigle.dwagger.annotations.ApiSupport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author haigle
 * @since dwagger v4.2.0
 */
public class ExtensionUtils {
    
    /**
     * 获取作者
     * @param apiOperationSupport 扩展注解实例
     * @return 作者
     */
    public static String getAuthors(ApiOperationSupport apiOperationSupport) {
        Set<String> authorSet = new HashSet<>();
        String author = apiOperationSupport.author();
        // 判断非空
        if (author != null && !"".equals(author) && !"null".equals(author)) {
            authorSet.add(author);
        }
        // 判断数组，since v4.2.0
        String[] authors = apiOperationSupport.authors();
        if (authors != null && authors.length > 0) {
            authorSet.addAll(Arrays.asList(authors));
        }
        if (!authorSet.isEmpty()) {
            return String.join(",", authorSet);
        }
        return "";
    }
    
    public static String getAuthor(ApiSupport apiSupport) {
        Set<String> authorSet = new HashSet<>();
        String author = apiSupport.author();
        // 判断非空
        if (author != null && !"".equals(author) && !"null".equals(author)) {
            authorSet.add(author);
        }
        // 判断数组，since v4.2.0
        String[] authors = apiSupport.authors();
        if (authors != null && authors.length > 0) {
            authorSet.addAll(Arrays.asList(authors));
        }
        if (!authorSet.isEmpty()) {
            return String.join(",", authorSet);
        }
        return "";
    }
    
}
