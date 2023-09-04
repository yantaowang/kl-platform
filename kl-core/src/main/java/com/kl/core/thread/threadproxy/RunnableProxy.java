package com.kl.core.thread.threadproxy;


import com.kl.core.thread.KlThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RunnableProxy implements InvocationHandler {
    private Runnable runnable;
    private String traceId;
    private Integer tenantId;

    public RunnableProxy(Runnable runnable, String traceId, Integer tenantId) {
        this.runnable = runnable;
        this.traceId = traceId;
        this.tenantId = tenantId;
    }

    public RunnableProxy() {
    }

    public RunnableProxy(Runnable runnable) {
        this.runnable = runnable;
        this.traceId = MDC.get("trace_id");
        this.tenantId = KlThreadLocal.getTenantId();
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable creatRunnableProxy() {
        Runnable proxy = (Runnable) Proxy.newProxyInstance(this.runnable.getClass().getClassLoader(), this.runnable.getClass().getInterfaces(), this);
        return proxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("run".equals(method.getName())) {
            Object var4;
            try {
                if (!StringUtils.isEmpty(this.traceId)) {
                    MDC.put("trace_id", this.traceId);
                }

                if (this.tenantId != null) {
                    KlThreadLocal.setTenantId(this.tenantId);
                }

                var4 = method.invoke(this.runnable, args);
            } finally {
                MDC.remove("trace_id");
                KlThreadLocal.remove();
            }

            return var4;
        } else {
            return method.invoke(this.runnable, args);
        }
    }
}
