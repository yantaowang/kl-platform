package com.kl.core.config;


import com.kl.core.thread.KlThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * @className : TaskExecutePoolAutoConfiguration
 * @since : 2021/09/14 下午2:48
 */
@Configuration
@EnableAsync
@Slf4j
public class TaskExecutePoolAutoConfiguration implements AsyncConfigurer {

    @Value("${task.pool.corePoolSize:4}")
    private int corePoolSize;

    @Value("${task.pool.maxPoolSize:10}")
    private int maxPoolSize;

    @Value("${task.pool.keepAliveSeconds:60}")
    private int keepAliveSeconds;

    @Value("${task.pool.queueCapacity:1024}")
    private int queueCapacity;

    @Bean
    public Executor getAsyncExecutor() {
        return KlThreadPoolTaskExecutor.newInstance(corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity, "taskExecutor-");
    }

    /**
     * 异步任务中异常处理
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
                log.error("[getAsyncUncaughtExceptionHandler] error method:{}", method.getName(), throwable);
            }
        };
    }
}