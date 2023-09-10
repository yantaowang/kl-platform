package com.kl.monitor.starter.config;

import com.kl.monitor.starter.monitor.MonitorMetrics;
import com.kl.monitor.starter.monitor.MonitorMetricsFetcher;
import com.kl.monitor.starter.props.MonitorMetricsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author guanjunpu
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = MonitorMetricsProperties.class)
@ConditionalOnProperty(prefix = "kl.monitor", value = "enabled", havingValue = "true")
public class MonitorAutoConfig {

	@Bean
	@ConditionalOnMissingBean(MonitorMetrics.class)
	public MonitorMetrics monitorMetrics(MonitorMetricsProperties monitorMetricsProperties,
										 List<MonitorMetricsFetcher> monitorMetricsFetcherList) {
		log.info("MonitorMetrics load");
		MonitorMetrics monitorMetrics = new MonitorMetrics();
		monitorMetrics.setMonitorMetricsProperties(monitorMetricsProperties);
		monitorMetrics.setMonitorMetricsFetcherList(monitorMetricsFetcherList);
		monitorMetrics.setMonitorMetricsTaskExecutor(monitorMetricsTaskExecutor());
		return monitorMetrics;
	}

	private TaskExecutor monitorMetricsTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		int cpuNum = Runtime.getRuntime().availableProcessors();
		log.info("当前机器核数:{}", cpuNum);
		taskExecutor.setCorePoolSize(cpuNum);
		taskExecutor.setMaxPoolSize(cpuNum * 2);
		taskExecutor.setQueueCapacity(200);
		taskExecutor.setKeepAliveSeconds(60);
		taskExecutor.setThreadNamePrefix("monitorMetricsTaskExecutor--");
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
		taskExecutor.setWaitForTasksToCompleteOnShutdown(false);
		taskExecutor.initialize();
		return taskExecutor;
	}
}
