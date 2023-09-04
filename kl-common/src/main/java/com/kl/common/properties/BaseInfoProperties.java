package com.kl.common.properties;

import com.kl.common.constants.CommonConstants;
import com.kl.common.thread.KlThreadLocal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 基本信息设置
 *
 */
@Slf4j
@Configuration
public class BaseInfoProperties implements InitializingBean {

    @Getter
    @Value(CommonConstants.GET_MODE_SPEL)
    private String mode;

    @Getter
    @Value("${kl.partner.enable:true}")
    private String enablePartner;

    @Getter
    @Value("${spring.application.name}")
    private String moduleName;

    @Override
    public void afterPropertiesSet() {
        log.info("当前服务名称: {}, 当前MODE: {}, 是否开多租户: {}", moduleName, mode, enablePartner);
        CommonConstants.APPLICATION_NAME = moduleName;
        KlThreadLocal.initConfig(getEnablePartnerBoolean(), mode);
    }

    public boolean getEnablePartnerBoolean() {
        return Boolean.TRUE.toString().equalsIgnoreCase(enablePartner);
    }
}
