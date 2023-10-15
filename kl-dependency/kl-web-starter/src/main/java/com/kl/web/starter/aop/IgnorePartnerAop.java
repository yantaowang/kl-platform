package com.kl.web.starter.aop;


import com.kl.common.thread.KlThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(10)
@Slf4j
public class IgnorePartnerAop {


    /**
     * 拦截IgnorePartner
     */
    @Around("@annotation(com.kl.web.starter.annotation.IgnorePartner) && within(com.kl..controller.*)")
    public Object verifyRoleExecuteCommand(ProceedingJoinPoint pjp) throws Throwable {
        // 忽略合作方sql拦截
        KlThreadLocal.ignorePartner();
        try {
            return pjp.proceed();// 执行方法
        } finally {
            KlThreadLocal.remove();
        }
    }

}