package com.kl.core.dubbo.filter;

import com.kl.core.util.TracIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.MDC;

import java.lang.reflect.Method;

/**
 * 异常处理
 */
@Activate(group = CommonConstants.PROVIDER, order = 4)
@Slf4j
public class DubboExceptionFilter extends ListenableFilter {

    public DubboExceptionFilter() {
        super.listener = new ExceptionListener();
    }

    /**
     * Does not need to override/implement this method.
     *
     * @param invoker
     * @param invocation
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        return invoker.invoke(invocation);
    }

    static class ExceptionListener implements Listener {

        @Override
        public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
            String traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
            String dubboFrom = RpcContext.getContext().getRemoteApplicationName();
            boolean ifRemove = false;
            if (StringUtils.isBlank(traceId)) {
                ifRemove = true;
                traceId = RpcContext.getContext().getAttachment(TracIdUtil.LOGGER_ID_PARAM_NAME);
                if (StringUtils.isBlank(traceId)) {
                    traceId = TracIdUtil.creaTracId();

                }
                MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
            }
            try {
                if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
                    try {
                        Throwable exception = appResponse.getException();
                        MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_EXCEPTION, "exception", exception.getClass().getSimpleName(), "type", InterfaceTypeEnum.RPC.getType(), "resource", DubboUtil.getMethodResourceName(invoker, invocation, false));

                        if (exception instanceof WarnEwpException) {
                            log.warn("[DUBBO_EXCEPTION_LOG] DubboFrom:[" + dubboFrom + "] Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + exception.getClass().getSimpleName() + ": " + exception.getMessage(), exception);
                        }else{
                            log.error("[DUBBO_EXCEPTION_LOG] DubboFrom:[" + dubboFrom + "] Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + exception.getClass().getSimpleName() + ": " + exception.getMessage(), exception);
                        }
                        if (exception instanceof EwpException) {
                            // 自定义异常直接抛出
                            return;
                        }

                        // directly throw if it's checked exception
                        if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                            return;
                        }
                        // directly throw if the exception appears in the signature
                        try {
                            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                            Class<?>[] exceptionClassses = method.getExceptionTypes();
                            for (Class<?> exceptionClass : exceptionClassses) {
                                if (exception.getClass().equals(exceptionClass)) {
                                    return;
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_EXCEPTION, "exception", exception.getClass().getSimpleName(), "type", InterfaceTypeEnum.RPC.getType(), "resource", DubboUtil.getMethodResourceName(invoker, invocation, false));
                            return;
                        }

                        // for the exception not found in method's signature, print ERROR message in server's log.

                        // directly throw if exception class and interface class are in the same jar file.
                        String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                        String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                        if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                            return;
                        }
                        // directly throw if it's JDK exception
                        String className = exception.getClass().getSimpleName();
                        if (className.startsWith("java.") || className.startsWith("javax.")) {
                            return;
                        }
                        // directly throw if it's dubbo exception
                        if (exception instanceof RpcException) {
                            return;
                        }

                        // otherwise, wrap with RuntimeException and throw back to the client
                        appResponse.setException(new RuntimeException(StringUtils.toString(exception)));
                        return;
                    } catch (Throwable e) {
                        MonitorUtil.reportCount(CommonMonitorEnum.APP_REQUEST_EXCEPTION, "exception", e.getClass().getSimpleName(), "type", InterfaceTypeEnum.RPC.getType(), "resource", DubboUtil.getMethodResourceName(invoker, invocation, false));
                        log.warn("[DUBBO_EXCEPTION_LOG] DubboFrom:[" + dubboFrom + "]Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                        return;
                    }
                }
            } finally {
                if (ifRemove) {
                    MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
                }
            }
        }

        @Override
        public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
            try {
                String traceId = RpcContext.getContext().getAttachment(TracIdUtil.LOGGER_ID_PARAM_NAME);
                if (StringUtils.isBlank(traceId)) {
                    traceId = TracIdUtil.creaTracId();

                }
                MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
                String dubboFrom = RpcContext.getContext().getRemoteApplicationName();
                if (e instanceof WarnEwpException) {
                    log.warn("[DUBBO_EXCEPTION_LOG] DubboFrom:[" + dubboFrom + "] Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }else{
                    log.error("[DUBBO_EXCEPTION_LOG] DubboFrom:[" + dubboFrom + "] Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }
            } finally {
                MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
            }
        }
    }
}
