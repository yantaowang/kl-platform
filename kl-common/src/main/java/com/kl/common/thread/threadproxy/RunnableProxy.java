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

/**
 * 线程代理
 */
public class RunnableProxy implements InvocationHandler {

    // 被代理对象
    private Runnable runnable;

    private String traceId;

    // 合作方标识
    private Integer partnerCode;

    public RunnableProxy(Runnable runnable, String traceId, Integer partnerCode) {
        this.runnable = runnable;
        this.traceId = traceId;
        this.partnerCode = partnerCode;
    }

    public RunnableProxy() {
    }

    public RunnableProxy(Runnable runnable) {
        this.runnable = runnable;
        this.traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
        this.partnerCode = KlThreadLocal.getPartnerCode();
    }

    public Runnable getRunnable() {

        return runnable;
    }

    public void setRunnable(Runnable runnable) {

        this.runnable = runnable;
    }

    public Runnable creatRunnableProxy() {

        Runnable proxy = (Runnable) Proxy.newProxyInstance(runnable.getClass().getClassLoader(), runnable.getClass().getInterfaces(), this);
        return proxy;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("run".equals(method.getName())) {
            try {
                if (!StringUtils.isEmpty(traceId)) {
                    MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
                }
                if (partnerCode != null) {
                    KlThreadLocal.setPartnerCode(partnerCode);
                }
                return method.invoke(runnable, args);
            } catch (Throwable t){
                MonitorUtil.reportCount(CommonMonitorEnum.THREAD_REQUEST_EXCEPTION,"exception",t.getClass().getSimpleName());
                throw  t;
            }finally {
                MonitorUtil.reportCount(CommonMonitorEnum.THREAD_REQUEST_COUNT);
                KlThreadLocal.remove();
                MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
            }
        }
        return method.invoke(runnable, args);
    }
}
