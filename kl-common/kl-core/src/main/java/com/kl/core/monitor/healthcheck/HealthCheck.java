package com.kl.core.monitor.healthcheck;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 服务健康检测，子类只需要实现该接口即可
 */
@Slf4j
public abstract class HealthCheck {

    // 请求超时时间 毫秒
    protected static final int REQUEST_TIMEOUT = 3000;

    public HealthCheck(Class clas) {
        log.info("[HealthCheckBeanInit] for class:{}", clas);
    }

    /**
     * 健康检测
     *
     * @return
     */
    public abstract String doHealthCheck(Map<String, String> params);
}