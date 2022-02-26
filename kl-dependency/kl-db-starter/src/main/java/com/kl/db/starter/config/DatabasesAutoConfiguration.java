package com.kl.db.starter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.kl.core.constants.CommonConstants;
import com.kl.db.starter.interceptor.TenantLineInnerInterceptor;
import com.kl.db.starter.mybatisplus.MybatisPlusPartnerLineHandler;
import com.kl.db.starter.snowflake.DSnowflakeWorkerAutoService;
import com.kl.db.starter.snowflake.DSnowflakeWorkerRunner;
import com.kl.db.starter.template.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author : yzy
 * 数据库相关配置
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "mybatis-plus.mapper-locations")
@AutoConfigureAfter({DSnowflakeWorkerAutoConfiguration.class, DataSource.class})
@Import(JdbcTemplate.class)
public class DatabasesAutoConfiguration {


    @Value("${spring.profiles.active}")
    private final String mode = null;

    @Value("${ewp.partner.enable:true}")
    private String enablePartner;

    /**
     * 初始化忽略合作方的表
     *
     */
    @Value("#{'${ewp.partner.ignoreTables:}'.split(',')}")
    public void ignoreTables(List<String> ignorePartnerTables) {
        System.setProperty("druid.mysql.usePingMethod", "false");
        ignorePartnerTables.remove("");
        ignorePartnerTables.add(CommonConstants.SNOWFLAKE_WORKER_TABLE);
        log.info("[IgnorePartnerTableConfigInit] 初始化合作方表配置:{}", ignorePartnerTables);
        IgnorePartnerTableConfig.IGNORE_TABLES = ignorePartnerTables;
    }

    /**
     * MybatisPlus 拦截器-支持分页
     */
    @Bean
    @ConditionalOnProperty(name = "mybatis-plus.mapper-locations")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if ("true".equals(enablePartner)) {
            log.info("[EnablePartnerInterceptor]启用数据库多合作方拦截插件");
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new MybatisPlusPartnerLineHandler(mode)));
        }
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 初始化雪花workid 生成器
     *
     */
    @Bean
    public DSnowflakeWorkerRunner dSnowflakeWorkerRunner(DSnowflakeWorkerAutoService snowflakeWorkerAutoService) {
        return new DSnowflakeWorkerRunner(snowflakeWorkerAutoService);
    }

}
