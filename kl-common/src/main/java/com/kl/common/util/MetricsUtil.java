package com.kl.common.util;

import com.kl.common.thread.KlThreadLocal;
import io.micrometer.core.instrument.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

/**
 * @author Mr.H
 *
 * 官方文档 https://micrometer.io/docs
 *
 * partnerCode 当前工具类已添加(如果EwpThreadLocal里没有partnerCode 则需要手动添加)
 */
public class MetricsUtil {

    public static Counter counter(String name, List<Tag> tags) {
        addTags(tags);
        return Metrics.counter(name, tags);
    }

    public static <T> T gauge(String name, List<Tag> tags, T obj, ToDoubleFunction<T> valueFunction) {
        addTags(tags);
        return Metrics.gauge(name, tags, obj, valueFunction);
    }

    public static <T extends Number> T gauge(String name, List<Tag> tags, T number) {
        addTags(tags);
        return Metrics.gauge(name, tags, number);
    }

    public static <T extends Number> T gauge(String name, T number) {
        List<Tag> tags = new ArrayList<>();
        addTags(tags);
        return Metrics.gauge(name, tags, number);
    }

    public static <T> T gauge(String name, T obj, ToDoubleFunction<T> valueFunction) {
        List<Tag> tags = new ArrayList<>();
        addTags(tags);
        return Metrics.gauge(name, tags, obj, valueFunction);
    }

    public static <T extends Collection<?>> T gaugeCollectionSize(String name, List<Tag> tags, T collection) {
        addTags(tags);
        return Metrics.gaugeCollectionSize(name, tags, collection);
    }

    public static <T extends Map<?, ?>> T gaugeMapSize(String name, List<Tag> tags, T map) {
        addTags(tags);
        return Metrics.gaugeMapSize(name, tags, map);
    }

    public static LongTaskTimer longTaskTimer(String name, List<Tag> tags) {
        addTags(tags);
        return Metrics.more().longTaskTimer(name, tags);
    }

    public static <T> FunctionCounter counter(String name, List<Tag> tags, T obj, ToDoubleFunction<T> countFunction) {
        addTags(tags);
        return Metrics.more().counter(name, tags, obj, countFunction);
    }

    public static <T extends Number> FunctionCounter counter(String name, List<Tag> tags, T number) {
        addTags(tags);
        return Metrics.more().counter(name, tags, number);
    }

    public static <T> TimeGauge timeGauge(String name, List<Tag> tags, T obj, TimeUnit timeFunctionUnit, ToDoubleFunction<T> timeFunction) {
        addTags(tags);
        return Metrics.more().timeGauge(name, tags, obj, timeFunctionUnit, timeFunction);
    }

    public static <T> FunctionTimer timer(String name, List<Tag> tags, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        addTags(tags);
        return Metrics.more().timer(name, tags, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit);
    }

    public static DistributionSummary summary(String name, List<Tag> tags) {
        addTags(tags);
        return Metrics.summary(name, tags);
    }

    private static void addTags(List<Tag> tags) {
        Integer partnerCode = KlThreadLocal.getPartnerCode();
        if (Objects.nonNull(partnerCode)) {
            if (null == tags) {
                tags = new ArrayList<>();
            }
            tags.add(Tag.of("partnerCode", partnerCode.toString()));
        }
    }
}

