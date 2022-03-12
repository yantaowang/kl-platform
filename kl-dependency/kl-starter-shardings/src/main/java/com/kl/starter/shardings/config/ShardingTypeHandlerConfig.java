package com.kl.starter.shardings.config;


import com.kl.starter.shardings.handler.ShardingLocalDateTimeTypeHandler;
import com.kl.starter.shardings.handler.ShardingLocalDateTypeHandler;
import com.kl.starter.shardings.handler.ShardingLocalTimeTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingTypeHandlerConfig {
    public ShardingTypeHandlerConfig() {
    }

    @Bean
    public ShardingLocalDateTimeTypeHandler localDateTimeTypeHandler() {
        return new ShardingLocalDateTimeTypeHandler();
    }

    @Bean
    public ShardingLocalDateTypeHandler localDateTypeHandler() {
        return new ShardingLocalDateTypeHandler();
    }

    @Bean
    public ShardingLocalTimeTypeHandler localTimeTypeHandler() {
        return new ShardingLocalTimeTypeHandler();
    }
}
