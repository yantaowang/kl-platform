package com.kl.db.starter.config;

import com.kl.db.starter.snowflake.DSnowflakeWorkerAutoService;
import com.kl.db.starter.snowflake.data.mapper.TSnowflakeWorkerMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 雪花算法自动配置
 * @since : 2021/09/12 下午10:34
 */
@Configuration
@MapperScan({"com.kl.db.starter.snowflake.data.*"})
@ConditionalOnProperty(name = "mybatis-plus.mapper-locations")
@Slf4j
public class DSnowflakeWorkerAutoConfiguration {

    @Bean
    @DependsOn({"TSnowflakeWorkerMapper"})
    public DSnowflakeWorkerAutoService snowflakeWorkerAutoService(TSnowflakeWorkerMapper dSnowflakeWorkerAutoDAO) {
        return new DSnowflakeWorkerAutoService(dSnowflakeWorkerAutoDAO);
    }
}