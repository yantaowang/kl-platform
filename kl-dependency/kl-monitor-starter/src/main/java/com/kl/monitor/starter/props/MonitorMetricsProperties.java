package com.kl.monitor.starter.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanjunpu
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ewp.monitor")
public class MonitorMetricsProperties {
	private String include;
}
