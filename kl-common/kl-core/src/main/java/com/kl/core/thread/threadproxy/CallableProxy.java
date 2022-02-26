package com.kl.core.thread.threadproxy;


import com.kl.core.thread.KlThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

public class CallableProxy<T> implements InvocationHandler {
    private Callable<T> callable;
    private String traceId;
    private Integer tenantId;

    public CallableProxy(Callable<T> callable, String traceId, Integer tenantId) {
        this.callable = callable;
        this.traceId = traceId;
        this.tenantId = tenantId;
    }

    public CallableProxy(Callable<T> callable) {
        this.callable = callable;
        this.traceId = MDC.get("trace_id");
        this.tenantId = KlThreadLocal.getTenantId();
    }

    public CallableProxy() {
    }

    public Callable<T> getCallable() {
        return this.callable;
    }

    public void setCallable(Callable<T> callable) {
        this.callable = callable;
    }

    public Callable<T> creatCallableProxy() {
        Callable<T> proxy = (Callable) Proxy.newProxyInstance(this.callable.getClass().getClassLoader(), this.callable.getClass().getInterfaces(), this);
        return proxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("call".equals(method.getName())) {
            Object var4;
            try {
                if (this.tenantId != null) {
                    KlThreadLocal.setTenantId(this.tenantId);
                }

                if (!StringUtils.isEmpty(this.traceId)) {
                    MDC.put("trace_id", this.traceId);
                }

                var4 = method.invoke(this.callable, args);
            } finally {
                MDC.remove("trace_id");
                KlThreadLocal.remove();
            }

            return var4;
        } else {
            return method.invoke(this.callable, args);
        }
    }
}

