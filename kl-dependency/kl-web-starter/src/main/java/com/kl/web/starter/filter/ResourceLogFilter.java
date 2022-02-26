package com.kl.web.starter.filter;


import com.alibaba.fastjson.JSON;
import com.kl.core.constants.CommonConstants;
import com.kl.core.constants.RunMode;
import com.kl.core.enums.ResponseCode;
import com.kl.core.exception.KlException;
import com.kl.core.exception.WarnKlException;
import com.kl.core.model.KlResponse;
import com.kl.web.starter.filter.wrapper.RequestWrapper;
import com.kl.web.starter.filter.wrapper.ResponseWrapper;
import com.kl.web.starter.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kl.core.constants.CommonConstants.THE_LOG_DATA_IS_TOO_LONG;

/**
 * 请求日志记录filter
 */
@Slf4j
public class ResourceLogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ResourceLogFilter.class);
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    // 不记录日志的URL
    public static List<String> NO_LOG_URL = new ArrayList<>();

    /**
     * 运行模式
     */
    @Value("${spring.profiles.active}")
    private String mode;

    public ResourceLogFilter() {
    }

    public ResourceLogFilter(List<String> NO_LOG_URL) {
        ResourceLogFilter.NO_LOG_URL = NO_LOG_URL;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            long start = System.currentTimeMillis();
            String url = httpRequest.getRequestURI();
            String referer = httpRequest.getHeader("Referer");
            String method = httpRequest.getMethod();
            String contentType = httpRequest.getContentType();
            StringBuilder info = new StringBuilder(method).append(":").append(url);
            info = info.append(",ip:").append(IPUtils.getIP(httpRequest));

            RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request,
                    contentType);
            ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
            if (POST.equals(method) || PUT.equals(method)) {
                if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType) ||
                        MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType) ||
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)) {

                    int contentLength = requestWrapper.getContentLength();
                    String body;
                    if (contentLength > 1024 * 1024) {
                        body = "消息体超过1Mb，暂不打印，content-length：" + contentLength;
                    } else {
                        body = requestWrapper.getBodyStr(url, contentType);
                    }
                    if (StringUtils.isNotEmpty(httpRequest.getQueryString())) {
                        info.append("?").append(httpRequest.getQueryString());
                    }
                    info.append(",body:").append(body);
                    info.append(",content-length：").append(contentLength);
//                    logger.info(info.toString());
//                    filterChain.doFilter(requestWrapper, response);
                } else {
                    info.append(",contentType:").append(contentType);
//                    logger.info(info.toString());
//                    filterChain.doFilter(request, response);
                }
            } else {
                if (StringUtils.isNotEmpty(httpRequest.getQueryString())) {
                    info.append("?").append(httpRequest.getQueryString());
                }
//                logger.info(info.toString());
//                filterChain.doFilter(request, response);
            }
            info.append(",[referer]:").append(referer);
            log.info("[httpRequestLog] param:{},{}", info.toString(), contentType);
            filterChain.doFilter(requestWrapper, responseWrapper);

            String responseStr = "no response content";
            if (responseWrapper.getContentType() != null
                    && MediaType.APPLICATION_JSON_VALUE.equals(responseWrapper.getContentType().split(";")[0])
                    && ((HttpServletResponse) response).getStatus() == HttpStatus.OK.value()) {
                responseStr = new String(responseWrapper.toByteArray(), response.getCharacterEncoding());
                long takeTime = System.currentTimeMillis() - start;
                if (responseStr.length() < CommonConstants.MAX_LOG_BODY_LENGTH) {
                    log.info("[httpResponseLog] [requestUrl]:{} result:{},{}", url, responseStr, checkResponseTime(takeTime));
                } else {
                    log.info("[httpResponseLog] [requestUrl]:{} result:{},{}", url, THE_LOG_DATA_IS_TOO_LONG, checkResponseTime(takeTime));
                }
            }
            response.getOutputStream().write(responseWrapper.toByteArray());
            response.getOutputStream().close();
        } catch (Exception e) {
            handleUnexpectedServerError(e, httpRequest, httpResponse);
        }
    }

    /**
     * 转换花费时间
     *
     * @param takeTime
     * @return
     */
    private String checkResponseTime(long takeTime) {
        if (takeTime < 1000) {
            return String.valueOf(takeTime);
        }
        if (takeTime < 2000) {
            return CommonConstants.TAKE_MORETHAN_ONE_SECOND + takeTime;
        }
        return CommonConstants.TAKE_MORETHAN_TWO_SECOND + takeTime;
    }

    @Override
    public void destroy() {
        //this is a empty method
    }

    /**
     * 通用异常处理
     *
     * @param throwable
     * @param request
     * @param httpResponse
     */
    private void handleUnexpectedServerError(Exception throwable, HttpServletRequest request, HttpServletResponse httpResponse) {
        // 打印日志提示
        if (Objects.nonNull(request) && throwable instanceof WarnKlException) {
            log.warn("#UnexpectedServerError request URI:{}, referer:{}", request.getRequestURI(),
                    request.getHeader("Referer"));
        } else if (Objects.nonNull(request)) {
            log.error("#UnexpectedServerError request URI:{}, referer:{}", request.getRequestURI(),
                    request.getHeader("Referer"));
        }

        if (throwable instanceof WarnKlException || (throwable.getCause() != null && throwable.getCause() instanceof WarnKlException)) {
            if (RunMode.PROD.equals(mode)) {
                log.warn("接口服务异常:" + throwable.getMessage());
            } else {
                log.warn("接口服务异常:", throwable);
            }
        } else if (throwable instanceof KlException || (throwable.getCause() != null && throwable.getCause() instanceof KlException)) {
            if (RunMode.PROD.equals(mode)) {
                log.error("接口服务异常:" + throwable.getMessage());
            } else {
                log.error("接口服务异常:", throwable);
            }
        } else {
            log.error("接口服务异常:", throwable);
        }

        // 处理异常信息
        KlResponse<Object> response = new KlResponse<>();
        if (throwable instanceof KlException) {
            KlException exception = (KlException) throwable;
            response.setCode(exception.getCode());
            response.setMessage(exception.getMessage());
            response.setException(exception.getMessage());
            if (Objects.nonNull(exception.getReturnData())) {
                response.setData(JSON.toJSONString(exception.getReturnData()));
            }
        } else if (throwable.getCause() != null && throwable.getCause() instanceof KlException) {
            KlException exception = (KlException) throwable.getCause();
            response.setCode(exception.getCode());
            response.setMessage(exception.getMessage());
            response.setException(exception.getMessage());
            if (Objects.nonNull(exception.getReturnData())) {
                response.setData(JSON.toJSONString(exception.getReturnData()));
            }
        } else {
            response.setCode(ResponseCode.COMMON_CODE.getCode());
            // 方便联调，打印一下日志
            response.setMessage(ResponseCode.COMMON_CODE.getDescription());
        }

        // 设置默认消息
        if (response.getMessage() == null) {
            response.setMessage("数据请求失败");
        }

        // 返回应答数据
        writeResponse(httpResponse, response);
    }

    /**
     * 返回响应json数据
     *
     * @param httpResponse
     * @param response
     */
    private void writeResponse(HttpServletResponse httpResponse, KlResponse response) {
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", httpResponse.getHeader("Origin"));
        try {
            httpResponse.getWriter().write(JSON.toJSONString(response));
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (IOException e) {
            log.error("handle exception error", e);
        }
    }
}
