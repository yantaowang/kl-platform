package com.kl.monitor.starter.utils;

import com.ewp.starter.monitor.config.MonitorConfig;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

@Slf4j
public class MonitorUtil {
    private volatile static MonitorConfig monitorConfig;

    /**
     * 递增计数
     *
     * @param monitorEnum 监控项和描述
     * @param infos       其他需要告警出来的信息+
     */
    public static void reportCount(MonitorEnum monitorEnum, String... infos) {
        try {
            tagsNullReturnStringNull(infos);
            if (!monitorEnable()) {
                return;
            }
            Metrics.counter(monitorEnum.getName(), infos).increment();
        } catch (Exception e) {
            log.warn("指标上报异常:" + Arrays.toString(infos), e);
        }
    }

    /**
     * 某一个变化的数，与之前上报无关，不做累加，如连接池的大小
     *
     * @param monitorEnum
     * @param obj         取值的对象
     * @param function    取值的方法
     * @param tags        标签，如数据连接池，可以给连接池名称
     * @param <T>
     */
    public static <T> void gauge(MonitorEnum monitorEnum, @Nullable T obj, ToDoubleFunction<T> function, String... tags) {
        try {
            tagsNullReturnStringNull(tags);
            if (!monitorEnable()) {
                return;
            }
            Gauge.builder(monitorEnum.getName(), obj, function).tags(tags).register(Metrics.globalRegistry);
        } catch (Exception e) {
            log.warn("指标上报异常:"+ Arrays.toString(tags),e);
        }
    }

    public static void setMonitorConfig(MonitorConfig config) {
        monitorConfig = config;
    }

    /**
     * 获取监控全局开关
     *
     * @return
     */
    public static boolean monitorEnable() {
        return monitorConfig == null || monitorConfig.getEnable() == 1;
    }

    /**
     * 执行耗时
     *
     * @param monitorEnum
     * @param o           要执行的方法
     * @param tags
     * @param <T>
     * @return
     */
    public static <T> T timer(MonitorEnum monitorEnum, Supplier<T> o, String... tags) {
        tagsNullReturnStringNull(tags);
        if (!monitorEnable()) {
            return o.get();
        }
        Timer timer = Metrics.timer(monitorEnum.getName(), tags);
        return timer.record(o);
    }

    public static void timer(MonitorEnum monitorEnum, Runnable o, String... tags) {
        tagsNullReturnStringNull(tags);
        if (!monitorEnable()) {
            o.run();
            return;
        }
        Timer timer = Metrics.timer(monitorEnum.getName(), tags);
        timer.record(o);
    }

    public static <T> T timerException(MonitorEnum monitorEnum, Callable<T> o, String... tags) throws Exception {
        tagsNullReturnStringNull(tags);
        if (!monitorEnable()) {
            return o.call();
        }
        Timer timer = Metrics.timer(monitorEnum.getName(), tags);
        return timer.recordCallable(o);
    }

    private static void tagsNullReturnStringNull(String[] tags) {
        if (tags == null || tags.length == 0) {
            return;
        }
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] == null) {
                tags[i] = "null";
            }
        }
    }

    public static void requestBucket(InterfaceTypeEnum type, long start) {
        try {
            long endTime = System.currentTimeMillis();
            String interfaceType = Objects.isNull(type) ? "-" : type.getType();
            long resultTime = endTime - start;
            String requestBucket = RequestBucketEnum.getEnumByTime(resultTime);
            CommonMonitorEnum baseRequestCountBucket = CommonMonitorEnum.BASE_REQUEST_COUNT_BUCKET;
            Metrics.counter(baseRequestCountBucket.getName(), "type",interfaceType, "bucket", requestBucket).increment();
        } catch (Exception e) {
            log.warn("指标上报异常,base_request_count_bucket,");
        }
    }
}
