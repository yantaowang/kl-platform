package com.kl.core.config;


import cn.hutool.extra.spring.SpringUtil;
import com.kl.core.constants.CommonConstants;
import com.kl.core.thread.KlThreadLocal;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * openFeign 多合作方全局开关处理
 *
 * @className : FeignConfiguration
 * @since : 2021/10/12 下午4:59
 */
@Configuration
@Slf4j
public class BaseCommonConfiguration {

    @Value("${spring.profiles.active}")
    private final String mode = null;

    @Value("${ewp.partner.enable:true}")
    private final String enablePartner = null;

    @Value("${spring.application.name}")
    private String moduleName;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initPartnerPlugin() {
        // 设置订阅者应用名称
        System.setProperty("project.name", moduleName);
        CommonConstants.APPLICATION_NAME = moduleName;
        KlThreadLocal.initConfig(enablePartner, mode);
    }

    @Bean
    public SpringUtil springUtilHutool() {
        return new SpringUtil();
    }


    /**
     * 将Nacos Client的监听信息加入
     */
    @Bean
    public CollectorRegistry collectorRegistry() {
        return CollectorRegistry.defaultRegistry;
    }
}