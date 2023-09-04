package com.kl.common.thread.threadproxy;

import com.kl.common.thread.KlThreadLocal;
import com.kl.common.util.TracIdUtil;
import com.kl.monitor.starter.utils.CommonMonitorEnum;
import com.kl.monitor.starter.utils.MonitorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

/**
 * 线程代理
 */
public class CallableProxy<T> implements InvocationHandler {

    // 被代理对象
    private Callable<T> callable;

    private String traceId;

    // 合作方标识
    private Integer partnerCode;

    public CallableProxy(Callable<T> callable, String traceId, Integer partnerCode) {
        this.callable = callable;
        this.traceId = traceId;
        this.partnerCode = partnerCode;
    }

    public CallableProxy(Callable<T> callable) {
        this.callable = callable;
        this.traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
        this.partnerCode = KlThreadLocal.getPartnerCode();
    }

    public CallableProxy() {
    }

    public Callable<T> getCallable() {

        return callable;
    }

    public void setCallable(Callable<T> callable) {

        this.callable = callable;
    }

    public Callable<T> creatCallableProxy() {

        Callable<T> proxy = (Callable) Proxy.newProxyInstance(callable.getClass().getClassLoader(), callable.getClass().getInterfaces(), this);
        return proxy;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("call".equals(method.getName())) {
            try {
                if (partnerCode != null) {
                    KlThreadLocal.setPartnerCode(partnerCode);
                }
                if (!StringUtils.isEmpty(traceId)) {
                    MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
                }
                return method.invoke(callable, args);
            }catch (Throwable t){
                MonitorUtil.reportCount(CommonMonitorEnum.THREAD_REQUEST_EXCEPTION,"exception",t.getClass().getSimpleName());
                throw  t;
            }finally {
                KlThreadLocal.remove();
                MonitorUtil.reportCount(CommonMonitorEnum.THREAD_REQUEST_COUNT);
                MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
            }
        }
        return method.invoke(callable, args);
    }
}
