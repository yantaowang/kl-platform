package com.kl.web.starter.filter;

import com.kl.core.constants.CommonConstants;
import com.kl.core.util.TracIdUtil;
import org.apache.dubbo.common.utils.StringUtils;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TraceIdFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader(CommonConstants.JWTModelKeyHeader.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = TracIdUtil.creaTracId();
        }
        MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
        }

    }

    @Override
    public void destroy() {
        MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
    }
}
