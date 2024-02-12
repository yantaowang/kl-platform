package com.kl.web.starter.filter;

import com.alibaba.fastjson.JSON;
import com.kl.common.constants.CommonConstants;
import com.kl.common.dto.KlResponse;
import com.kl.common.enums.ResponseCode;
import com.kl.common.exception.KlException;
import com.kl.common.exception.WarnKlException;
import com.kl.monitor.starter.utils.CommonMonitorEnum;
import com.kl.monitor.starter.utils.InterfaceTypeEnum;
import com.kl.monitor.starter.utils.MonitorUtil;
import com.kl.web.starter.filter.wrapper.RequestWrapper;
import com.kl.web.starter.filter.wrapper.ResponseWrapper;
import com.kl.web.starter.util.IPUtils;
import com.kl.web.starter.util.UriUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 请求日志记录filter
 */
@Slf4j
public class ResourceLogFilter implements Filter {
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    // 不记录日志的URL
    public static List<String> NO_LOG_URL = new ArrayList<>();

    /**
     * 运行模式
     */
    private String mode;

    /**
     * 请求ID字段
     */
    private String requestIdHeader;

    /**
     * 设置 lb requestIdheader、当前运行环境
     * bean不是自动动态装配，参数变量要传进来
     *
     * @param requestIdHeader
     * @param mode
     */
    public ResourceLogFilter(String requestIdHeader, String mode) {
        this.requestIdHeader = requestIdHeader;
        this.mode = mode;
    }

    public ResourceLogFilter(List<String> noLogUrl) {
        ResourceLogFilter.NO_LOG_URL = noLogUrl;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String responseStr = null;
        long start = System.currentTimeMillis();
        try {
            String url = httpRequest.getRequestURI();
            String referer = httpRequest.getHeader("Referer");
            String requestIdHeaderValue = httpRequest.getHeader(requestIdHeader);
            String method = httpRequest.getMethod();
            String contentType = httpRequest.getContentType();
            StringBuilder info = new StringBuilder(method).append(":").append(url)
                    .append(",ip:").append(IPUtils.getIP(httpRequest));

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
            info.append(",[requestId]:").append(requestIdHeaderValue);
            log.info("[httpRequestLog] param:{},{}", info, contentType);
            filterChain.doFilter(requestWrapper, responseWrapper);

            if (responseWrapper.getContentType() != null
                    && MediaType.APPLICATION_JSON_VALUE.equals(responseWrapper.getContentType().split(";")[0])
                    && ((HttpServletResponse) response).getStatus() == HttpStatus.OK.value()) {
                responseStr = new String(responseWrapper.toByteArray(), response.getCharacterEncoding());
                long takeTime = System.currentTimeMillis() - start;
                if (responseStr.length() < CommonConstants.MAX_LOG_BODY_LENGTH) {
                    log.info("[httpResponseLog] [requestUrl]:{} result:{},{}", url, responseStr, checkResponseTime(takeTime));
                } else {
                    log.info("[httpResponseLog] [requestUrl]:{} result:{},{}", url, CommonConstants.THE_LOG_DATA_IS_TOO_LONG, checkResponseTime(takeTime));
                }
            }
            response.getOutputStream().write(responseWrapper.toByteArray());
            response.getOutputStream().close();
        } catch (Exception e) {
            /**
             * 上报请求处理异常埋点指标
             */
            MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_EXCEPTION,"exception",e.getClass().getSimpleName(),"type", InterfaceTypeEnum.HTTP.getType(),"resource", UriUtil.getResourceName(httpRequest));
            handleUnexpectedServerError(e, httpRequest, httpResponse);
        } finally {
            try {
                String code = "unknow";
                if (StringUtils.isNotBlank(responseStr) && responseStr.length() < CommonConstants.MAX_LOG_BODY_LENGTH) {
                    try {
                        code = JSON.parseObject(responseStr).getString("code");
                        if (StringUtils.isBlank(code)) {
                            code = "unknow";
                        }
                    } catch (Throwable t) {
                    }
                }
                MonitorUtil.requestBucket(InterfaceTypeEnum.HTTP, start);
                /**
                 * 上报请求处理数
                 */
                MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_COUNT, "resource", UriUtil.getResourceName(httpRequest), "type", InterfaceTypeEnum.HTTP.getType(), "code", code);
            } catch (Throwable t) {
                log.warn("上报埋点异常", t);
            }
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
            log.warn("#UnexpectedServerError request URI:{}, referer:{}, msg:{}", request.getRequestURI(),
                    request.getHeader("Referer"), throwable.getMessage());
        } else if (Objects.nonNull(request)) {
            log.error("#UnexpectedServerError request URI:{}, referer:{}, msg:{}", request.getRequestURI(),
                    request.getHeader("Referer"), throwable == null ? "nullObj" : throwable.getMessage());
        }
        if (throwable instanceof WarnKlException || (throwable.getCause() != null && throwable.getCause() instanceof WarnKlException)) {
            log.warn("接口服务异常:", throwable);
        } else if (throwable instanceof WarnKlException || (throwable.getCause() != null && throwable.getCause() instanceof KlException)) {
            log.warn("接口服务异常:", throwable);
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
    private void writeResponse(HttpServletResponse httpResponse, KlResponse<?> response) {
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
