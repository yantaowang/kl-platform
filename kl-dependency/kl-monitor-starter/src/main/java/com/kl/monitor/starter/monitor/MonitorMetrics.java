package com.kl.monitor.starter.monitor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.kl.monitor.starter.condition.MonitorEnableCondition;
import com.kl.monitor.starter.props.MonitorMetricsProperties;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author guanjunpu
 */
@Slf4j
@Data
public class MonitorMetrics implements MeterBinder {

    private List<MonitorMetricsFetcher> monitorMetricsFetcherList;
    private MonitorMetricsProperties monitorMetricsProperties;
    private TaskExecutor monitorMetricsTaskExecutor;

    private static final Long WAIT_TIME = 2L;

    @Override
    public void bindTo(@NonNull MeterRegistry meterRegistry) {
        if (ObjectUtil.isNotEmpty(monitorMetricsFetcherList)) {
            monitorMetricsFetcherList.forEach(monitorMetricsFetcher -> {
                if (MonitorEnableCondition.matches(monitorMetricsProperties.getInclude(), monitorMetricsFetcher.getType())) {
                    registerMonitorMetricsFetcher(meterRegistry, monitorMetricsFetcher);
                }
            });
        }
    }

    /**
     * 注册单个 monitorMetricsFetcher
     *
     * @param meterRegistry         MeterRegistry
     * @param monitorMetricsFetcher MonitorMetricsFetcher
     * @return 注册的实际Gauge集合
     */
    public List<Gauge> registerMonitorMetricsFetcher(MeterRegistry meterRegistry, MonitorMetricsFetcher monitorMetricsFetcher) {
        Map<MonitorMetricsEntity, Function<MonitorMetricsFetcher, Double>> monitorFunctionMap = monitorMetricsFetcher.fetchMetrics();
        List<Gauge> gauges = new ArrayList<>(monitorFunctionMap.size());
        monitorFunctionMap.forEach((monitorMetricsEntity, function) -> {
            Gauge gauge = Gauge.builder(monitorMetricsFetcher.getType() + StrPool.C_DOT + monitorMetricsEntity.getName(),
                            monitorMetricsFetcher, monitorMetricsFetcher2 -> {
                                CountDownLatch countDownLatch = new CountDownLatch(1);
                                final Double[] metricResult = {1d};
                                final Thread[] threads = {null};
                                monitorMetricsTaskExecutor.execute(() -> {
                                    threads[0] = Thread.currentThread();
                                    metricResult[0] = function.apply(monitorMetricsFetcher2);
                                    countDownLatch.countDown();
                                });
                                try {
                                    boolean await = countDownLatch.await(WAIT_TIME, TimeUnit.SECONDS);
                                    // 超时截停线程
                                    if (!await && threads[0] != null && threads[0].isAlive()) {
                                        threads[0].interrupt();
                                    }
                                } catch (Throwable e) {
                                    log.warn("metrics thread failed", e);
                                }
                                return metricResult[0];
                            })
                    .description(monitorMetricsEntity.getDescription()).tags(monitorMetricsEntity.getTags())
                    .strongReference(true).register(meterRegistry);
            gauges.add(gauge);
        });
        return gauges;
    }

    /**
     * 解除注册的监控
     *
     * @param meterRegistry MeterRegistry
     * @param meterIds      取消监控的实体
     */
    public void unRegisterMonitorMetricsFetcher(MeterRegistry meterRegistry, List<Meter.Id> meterIds) {
        if (meterIds != null) {
            for (Meter.Id meterId : meterIds) {
                meterRegistry.remove(meterId);
            }
        }
    }

    /**
     * 解除注册的监控
     *
     * @param meterRegistry MeterRegistry
     * @param types         需要删除的type
     * @param meterChecker  自定义检查
     */
    public void unRegisterMonitorMetricsFetcher(MeterRegistry meterRegistry, List<String> types,
                                                MeterChecker meterChecker) {
        if (CollUtil.isEmpty(types)) {
            return;
        }

        Map<String, String> preMap = new HashMap<>(types.size() * 2);
        types.forEach(type -> {
            preMap.put(type + ".", type);
            preMap.put(type + "_", type);
        });

        List<Meter.Id> meterIds = new LinkedList<>();
        meterRegistry.forEachMeter(meter -> {
            Meter.Id id = meter.getId();
            String name = id.getName();
            preMap.keySet().stream().filter(name::startsWith).findFirst().ifPresent(pre -> {
                String type = preMap.get(pre);
                if (meterChecker == null || meterChecker.check(type, meter)) {
                    meterIds.add(id);
                }
            });
        });

        log.info("clean prometheus monitor info: {}", meterIds);
        for (Meter.Id meterId : meterIds) {
            meterRegistry.remove(meterId);
        }
    }
}