package com.kl.starter.kafka.aop;


import com.kl.core.util.TracIdUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

@Aspect
@Order(30)
public class KafkaConsumerAop {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerAop.class);

    public KafkaConsumerAop() {
    }

    @Around("execution(* com.kl..consumer.kafka.*.consume(..))")
    public Object interceptAndRecord(ProceedingJoinPoint pjp) throws Throwable {
        Object var3;
        try {
            String traceId = TracIdUtil.creaTracId();
            MDC.put("trace_id", traceId);
            var3 = pjp.proceed();
        } finally {
            MDC.remove("trace_id");
        }

        return var3;
    }
}

