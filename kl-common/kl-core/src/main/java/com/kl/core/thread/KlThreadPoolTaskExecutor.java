package com.kl.core.thread;

import com.kl.core.thread.threadproxy.CallableProxy;
import com.kl.core.thread.threadproxy.RunnableProxy;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义扩展线程池、支持自定义线程变量传递
 */
@Slf4j
public class KlThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = 8693217207544759425L;
    // 默认 队列容量
    private static final int DEFAULT_QUEUE_CAPACITY = 1024;
    // 默认 活跃时间
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;

    /**
     * 创建线程池
     *
     * @param corePoolSize     核心数
     * @param maxPoolSize      最大数
     * @param threadNamePrefix 线程名字前缀
     * @return ThreadPoolTaskExecutor
     */
    public static KlThreadPoolTaskExecutor newInstance(int corePoolSize, int maxPoolSize, String threadNamePrefix) {
        return newInstance(corePoolSize, maxPoolSize, DEFAULT_KEEP_ALIVE_SECONDS, DEFAULT_QUEUE_CAPACITY, threadNamePrefix);
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize     核心数
     * @param maxPoolSize      最大数
     * @param keepAliveSeconds 活跃时间
     * @param queueCapacity    队列容量
     * @param threadNamePrefix 线程名字前缀
     * @return ThreadPoolTaskExecutor
     */
    public static KlThreadPoolTaskExecutor newInstance(int corePoolSize, int maxPoolSize, int keepAliveSeconds, int queueCapacity, String threadNamePrefix) {
        log.info("[newEwpThreadPoolTaskExecutor] corePoolSize:{}, maxPoolSize:{} ,keepAliveSeconds:{}, queueCapacity:{}, threadNamePrefix:{}",
                corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity, threadNamePrefix);
        KlThreadPoolTaskExecutor executor = new KlThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列容量
        executor.setQueueCapacity(queueCapacity);
        //活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //线程名字前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 展示线程信息
     *
     * @param prefix
     */
    private void showThreadPoolInfo(String prefix) {
        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
        log.info("[VisiableThreadPoolTaskExecutorCheckInfo]{}, {},taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
                this.getThreadNamePrefix(), prefix, threadPoolExecutor.getTaskCount(), threadPoolExecutor.getCompletedTaskCount(),
                threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
        setThreadMetrics(threadPoolExecutor, this.getThreadNamePrefix());
    }

    private void setThreadMetrics(ThreadPoolExecutor threadPoolExecutor, String threadName) {
        Tag tag = Tag.of("thread.name", threadName);
        Metrics.gauge("thread.core.pool.size", Collections.singletonList(tag), threadPoolExecutor.getCorePoolSize());
        Metrics.gauge("thread.core.max.size", Collections.singletonList(tag), threadPoolExecutor.getMaximumPoolSize());
        Metrics.gauge("thread.queue.capacity", Collections.singletonList(tag), threadPoolExecutor.getQueue().size());
        Metrics.gauge("thread.keep.alive.seconds", Collections.singletonList(tag), threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS));
        Metrics.gauge("thread.active.count", Collections.singletonList(tag), threadPoolExecutor.getTaskCount());
        Metrics.gauge("thread.task.count", Collections.singletonList(tag), threadPoolExecutor.getActiveCount());
        Metrics.gauge("thread.completed.task.count", Collections.singletonList(tag), threadPoolExecutor.getCompletedTaskCount());
    }

    @Override
    public void execute(Runnable task) {
        showThreadPoolInfo("[DO_EXECUTE]");
        super.execute(new RunnableProxy(task)
                .creatRunnableProxy());
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        showThreadPoolInfo("[DO_EXECUTE_TIMEOUT]");
        super.execute(new RunnableProxy(task)
                .creatRunnableProxy(), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        showThreadPoolInfo("[DO_SUBMIT]");
        return super.submit(new RunnableProxy(task)
                .creatRunnableProxy());
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        showThreadPoolInfo("[DO_SUBMIT_TIMEOUT]");
        return super.submit(new CallableProxy(task)
                .creatCallableProxy());
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        showThreadPoolInfo("[DO_SUBMIT_LISTENABLE_RUNNABLE]");
        return super.submitListenable(new RunnableProxy(task)
                .creatRunnableProxy());
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        showThreadPoolInfo("[DO_SUBMIT_LISTENABLE_CALLABLE]");
        return super.submitListenable(new CallableProxy<>(task)
                .creatCallableProxy());
    }
}