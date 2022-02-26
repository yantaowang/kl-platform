package com.kl.core.monitor.healthcheck;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;
import com.kl.core.model.ServerStatus;
import com.kl.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * nacos 服务健康检测
 */
@Slf4j
public class NacosHealthCheck extends HealthCheck {

    @Value("${spring.application.name:}")
    public String applicationName;

    public String serverAddr;

    private List<String> serverList;

    public NacosHealthCheck() {
        super(NacosHealthCheck.class);
    }

    @Value("${spring.cloud.nacos.discovery.server-addr:}")
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
        if (StringUtils.isNotEmpty(serverAddr)) {
            this.serverList = Arrays.asList(serverAddr.split(","));
        }
    }

    /**
     * 健康检测
     *
     * @return
     */
    @Override
    public String doHealthCheck(Map<String, String> params) {
        log.info("[ServerStatus] [NacosHealthCheck]");
        try {
            for (String server : serverList) {
                String result = HttpUtil.get(server + UtilAndComs.nacosUrlBase + "/operator/metrics", HttpUtil.timeout);
                JSONObject json = JSON.parseObject(result);
                String serverStatus = json.getString("status");
                if (!"UP".equals(serverStatus)) {
                    log.error("[ServerStatus] nacos server:{} error response:{}", server, json);
                    return ServerStatus.ERROR;
                }
            }
        } catch (Exception e) {
            log.error("[ServerStatus] [NacosHealthCheck] error", e);
            return ServerStatus.ERROR;
        }
        return ServerStatus.OK;
    }
}
