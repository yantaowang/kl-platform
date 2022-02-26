package com.kl.web.starter.handle;


import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.kl.core.enums.ResponseCode;
import com.kl.core.model.KlResponse;
import com.kl.web.starter.util.ExceptionHandleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;

/**
 * 网站异常处理器类
 *
 * @author yzy
 */
@Slf4j
@ControllerAdvice
public class WebsiteExceptionHandler {

    /** 属性相关 */
    /**
     * 运行模式
     */
    @Value("${spring.profiles.active}")
    private String mode;

    /**
     * 处理参数验证异常
     *
     * @param exception 异常信息
     * @return knowledge应答
     */
    @ResponseBody
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class, UnexpectedTypeException.class})
    public KlResponse handleParameterValidException(Exception exception, HttpServletRequest request) {
        return ExceptionHandleUtil.handleParameterValidException(exception, request, mode);
    }

    /**
     * 处理未期望的服务异常
     *
     * @param exception 抛出异常
     * @return knowledge应答
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public KlResponse handleUnexpectedServerError(Exception exception, HttpServletRequest request) {
        return ExceptionHandleUtil.handleUnexpectedServerError(exception, request, mode);
    }

    /**
     * 处理未期望的服务异常
     *
     * @param exception 抛出异常
     * @return knowledge应答
     */
    @ResponseBody
    @ExceptionHandler(UndeclaredThrowableException.class)
    public KlResponse handleUndeclaredThrowableException(UndeclaredThrowableException exception, HttpServletRequest request) {
        return handleDegradeException(exception, request);
    }

    /**
     * sentinel 相关异常拦截
     *
     * @param exception
     * @param request
     * @return
     */
    private KlResponse handleDegradeException(UndeclaredThrowableException exception, HttpServletRequest request) {
        if (exception.getUndeclaredThrowable() != null &&
                (exception.getUndeclaredThrowable() instanceof DegradeException ||
                        exception.getUndeclaredThrowable() instanceof BlockException ||
                        exception.getUndeclaredThrowable() instanceof FlowException)) {
            if (Objects.nonNull(request)) {
                log.warn("[GlobalHandleUnexpectedServerError]#UnexpectedServerError request URI:{}, referer:{}", request.getRequestURI(),
                        request.getHeader("Referer"));
            }
            log.warn("[GlobalHandleUnexpectedServerError]触发sentinel流控:", exception);
            return KlResponse.failure(ResponseCode.SENTINEL_BLOCK);
        }
        return ExceptionHandleUtil.handleUnexpectedServerError(exception, request, mode);
    }
}
