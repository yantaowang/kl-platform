package com.kl.web.starter.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,HEAD,PUT,PATCH,DELETE,OPTION,OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Max-Age", "3600");

        if(RequestMethod.OPTIONS.name().equals(request.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}