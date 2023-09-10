package com.kl.monitor.starter.config;

import com.kl.monitor.starter.utils.MonitorUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 监控开关
 */
@Component
@Getter
@Setter
@Slf4j
@ConfigurationProperties(prefix = "starter.monitor")
public class MonitorConfig{
    private Integer enable;

    @PostConstruct
    private void init() {
        log.info("刷新监控开关："+enable);
        if(enable == null){
            enable = 1;
        }
        MonitorUtil.setMonitorConfig(this);
    }
}
