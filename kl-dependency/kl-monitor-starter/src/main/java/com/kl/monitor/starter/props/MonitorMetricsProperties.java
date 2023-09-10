package com.kl.monitor.starter.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kl.monitor")
public class MonitorMetricsProperties {
	private String include;
}
