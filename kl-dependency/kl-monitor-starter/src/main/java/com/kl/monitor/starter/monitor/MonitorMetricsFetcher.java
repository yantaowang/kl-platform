package com.kl.monitor.starter.monitor;

import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.function.Function;

public interface MonitorMetricsFetcher {

	/**
	 * 获取组件类型
	 * 
	 * @return
	 */
	@NonNull
	public String getType();

	/**
	 * 获取组件各指标获取方法定义
	 * 
	 * @return
	 */
	@NonNull
	public <T extends MonitorMetricsFetcher> Map<MonitorMetricsEntity, Function<T, Double>> fetchMetrics();
}
