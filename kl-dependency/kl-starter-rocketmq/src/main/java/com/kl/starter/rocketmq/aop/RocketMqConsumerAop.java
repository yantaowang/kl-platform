package com.kl.starter.rocketmq.aop;

import com.kl.core.constants.CommonConstants;
import com.kl.core.thread.KlThreadLocal;
import com.kl.core.util.TracIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

@Aspect
@Order(30)
@Slf4j
public class RocketMqConsumerAop {

    /**
     * 拦截rocketmq消费
     */
    @Around("execution(* com.kl..consumer.rocketmq.*.onMessage(..))")
    public Object interceptAndRecord(ProceedingJoinPoint pjp) throws Throwable {
        try {
            MessageExt messageExt = (MessageExt) pjp.getArgs()[0];
            String traceId = messageExt.getProperty(TracIdUtil.LOGGER_ID_PARAM_NAME);
            String tenantId = messageExt.getProperty(CommonConstants.TENANT_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = TracIdUtil.creaTracId();
            }
            if (StringUtils.isNotEmpty(tenantId)) {
                KlThreadLocal.setTenantId(Integer.parseInt(tenantId));
            }
            MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
            return pjp.proceed();
        } finally {
            KlThreadLocal.remove();
            MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
        }
    }

}