package com.kl.web.starter.filter;

import com.kl.common.constants.CommonConstants;
import com.kl.common.thread.KlThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class PartnerNumberFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = httpRequest.getHeader(CommonConstants.JWTModelKeyHeader.PARTNER_CODE);
        if (StringUtils.isBlank(tenantId)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            KlThreadLocal.setPartnerCode(Integer.parseInt(tenantId));
            chain.doFilter(request, response);
        } finally {
            KlThreadLocal.remove();
        }
    }
}