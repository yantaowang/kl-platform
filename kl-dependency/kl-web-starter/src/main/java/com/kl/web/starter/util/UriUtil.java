package com.kl.web.starter.util;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;


public class UriUtil {
    private final static SentinelWebMvcConfig config = new SentinelWebMvcConfig();

    /**
     * 获取http地址方法，获取代码里配置的地址，/api/test/{type}
     * @param request
     * @return
     */
    public static String getResourceName(HttpServletRequest request) {
        // Resolve the Spring Web URL pattern from the request attribute.
        Object resourceNameObject = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (resourceNameObject == null || !(resourceNameObject instanceof String)) {
            return null;
        }
        String resourceName = (String) resourceNameObject;
        UrlCleaner urlCleaner = config.getUrlCleaner();
        if (urlCleaner != null) {
            resourceName = urlCleaner.clean(resourceName);
        }
        // Add method specification if necessary
        if (StringUtil.isNotEmpty(resourceName) && config.isHttpMethodSpecify()) {
            resourceName = request.getMethod().toUpperCase() + ":" + resourceName;
        }
        return resourceName;
    }
}
