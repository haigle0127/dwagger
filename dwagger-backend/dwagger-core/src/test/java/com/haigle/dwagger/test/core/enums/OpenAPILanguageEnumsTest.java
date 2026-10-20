package com.haigle.dwagger.test.core.enums;

import com.haigle.dwagger.core.enums.OpenAPILanguageEnums;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * @since  4.0.0
 * @author haigle
 */
public class OpenAPILanguageEnumsTest {
    
    @Test
    public void testLang() {
        Locale locale = Locale.forLanguageTag(OpenAPILanguageEnums.ZH_CN.getValue());
        Assert.assertEquals(locale, Locale.CHINA);
        System.out.println(locale.toString());
        Locale locale1 = Locale.forLanguageTag(OpenAPILanguageEnums.EN.getValue());
        Assert.assertEquals(locale1, Locale.US);
        System.out.println(locale1.toString());
        Locale locale2 = Locale.forLanguageTag("zh-cn");
        System.out.println(locale2.toString());
    }
}
