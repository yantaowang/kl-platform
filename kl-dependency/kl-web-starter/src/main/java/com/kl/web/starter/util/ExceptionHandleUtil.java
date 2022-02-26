package com.kl.web.starter.util;


import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.kl.core.constants.RunMode;
import com.kl.core.enums.ResponseCode;
import com.kl.core.exception.KlException;
import com.kl.core.exception.WarnKlException;
import com.kl.core.model.KlResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 异常处理
 *
 * @className : ExceptionHandleUtil
 * @since : 2021/09/08 下午5:26
 */
@Slf4j
public class ExceptionHandleUtil {


    /**
     * 处理参数异常
     *
     * @param exception
     * @param request
     * @param mode
     * @return
     */
    public static KlResponse<Object> handleParameterValidException(Exception exception, HttpServletRequest request, String mode) {
        // 打印日志提示
        if (Objects.nonNull(request)) {
            log.warn("[ParameterValidException] request URI:{}, referer:{}", request.getRequestURI(),
                    request.getHeader("Referer"));
        }

        if (RunMode.PROD.equals(mode)) {
            log.warn("[ParameterValidException]参数验证异常:" + exception.getMessage());
        } else {
            log.warn("[ParameterValidException]参数验证异常:", exception);
        }

        // 获取绑定结果
        BindingResult bindingResult = null;
        if (exception instanceof BindException) {
            bindingResult = ((BindException) exception).getBindingResult();
        } else if (exception instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
        }

        StringBuilder sb = new StringBuilder();
        if (bindingResult != null && CollectionUtils.isNotEmpty(bindingResult.getAllErrors())) {
            boolean isFirst = true;
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(",");
                }
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    sb.append(fieldError.getField());
                    sb.append(fieldError.getDefaultMessage());
                } else {
                    sb.append(error.getObjectName());
                    sb.append(error.getDefaultMessage());
                }
            }
        }
        // 返回参数错误
        KlResponse<Object> response = new KlResponse();
        response.setCode(ResponseCode.PARAMETER_ERROR.getCode());
        response.setMessage("参数验证异常");
        response.setException(sb.toString());
        return response;
    }

    /**
     * 处理未期望的服务异常
     *
     * @param throwable 抛出异常
     * @return KlResponse应答
     */
    public static KlResponse<Object> handleUnexpectedServerError(Exception throwable, HttpServletRequest request, String mode) {
        // 打印日志提示
        if (Objects.nonNull(request)) {
            log.warn("[GlobalHandleUnexpectedServerError]#UnexpectedServerError request URI:{}, referer:{}", request.getRequestURI(),
                    request.getHeader("Referer"));
        }

        if (throwable instanceof WarnKlException) {
            WarnKlException warnKlException = (WarnKlException) throwable;
            if (RunMode.PROD.equals(mode)) {
                if (throwable.getCause() == null) {
                    log.warn("[GlobalHandleUnexpectedServerError]接口服务异常:{}", throwable.getMessage() + logSelfMessage(warnKlException));
                } else {
                    log.warn("[GlobalHandleUnexpectedServerError]接口服务异常:{}", logSelfMessage(warnKlException), throwable);
                }
            } else {
                log.warn("[GlobalHandleUnexpectedServerError]接口服务异常:", throwable);
            }
        } else if (throwable instanceof KlException) {
            KlException KlException = (KlException) throwable;
            if (RunMode.PROD.equals(mode)) {
                if (throwable.getCause() == null) {
                    log.error("[GlobalHandleUnexpectedServerError]接口服务异常:{}", throwable.getMessage() + logSelfMessage(KlException));
                } else {
                    log.error("[GlobalHandleUnexpectedServerError]接口服务异常:{}", logSelfMessage(KlException), throwable);
                }
            } else {
                log.error("[GlobalHandleUnexpectedServerError]接口服务异常:{}", logSelfMessage(KlException), throwable);
            }
        } else if (throwable instanceof HttpMessageNotReadableException) {
            // 客户端终止请求导致 https://blog.csdn.net/z69183787/article/details/85109959
            log.warn("[GlobalHandleUnexpectedServerError]接口服务异常:", throwable);
        } else if (throwable instanceof BlockException || throwable.getMessage() != null && throwable.getMessage().contains("SentinelBlockException:")) {
            log.warn("[GlobalHandleUnexpectedServerError]触发sentinel流控:", throwable);
            // 触发sentinel流控
            return KlResponse.failure(ResponseCode.SENTINEL_BLOCK);
        } else {
            log.error("[GlobalHandleUnexpectedServerError]接口服务异常:", throwable);
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
        return response;
    }

    /**
     * 获取扩展日志信息
     *
     * @param exception
     * @return
     */
    private static String logSelfMessage(KlException exception) {
        if (StringUtils.isEmpty(exception.getSelfMessage())) {
            return "";
        }
        return exception.getSelfMessage();
    }
}