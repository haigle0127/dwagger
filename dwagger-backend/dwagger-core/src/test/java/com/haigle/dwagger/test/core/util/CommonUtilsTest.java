package com.haigle.dwagger.test.core.util;

import com.haigle.dwagger.core.conf.GlobalConstants;
import com.haigle.dwagger.core.util.CommonUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @since 
 * @author haigle
 */

public class CommonUtilsTest {
    
    @Test
    public void testDebugUrl() {
        String host = "192.168.0.11:9090";
        Assert.assertEquals(GlobalConstants.PROTOCOL_HTTP + host, CommonUtils.getDebugUri(host));
        host = "https://192.168.0.11:9090";
        Assert.assertEquals(host, CommonUtils.getDebugUri(host));
        host = "http://192.168.0.11:9090";
        Assert.assertEquals(host, CommonUtils.getDebugUri(host));
        host = " http://192.168.0.11:9090";
        Assert.assertEquals(host.trim(), CommonUtils.getDebugUri(host));
        Assert.assertEquals(GlobalConstants.EMPTY_STR, CommonUtils.getDebugUri(""));
        Assert.assertEquals(GlobalConstants.EMPTY_STR, CommonUtils.getDebugUri(null));
    }
}
