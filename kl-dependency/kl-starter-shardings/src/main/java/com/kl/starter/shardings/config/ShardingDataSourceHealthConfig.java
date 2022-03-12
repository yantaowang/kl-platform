package com.kl.starter.shardings.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 解决sharding-jdb 不支持 spring boot 2.3.X的问题，
 * 重写数据源的健康检查机制
 */
@Configuration
@ConditionalOnClass(DataSourceHealthContributorAutoConfiguration.class)
public class ShardingDataSourceHealthConfig extends DataSourceHealthContributorAutoConfiguration {

    @Value("${spring.datasource.dbcp2.validation-query:select 1}")
    private String defaultQuery;

    public ShardingDataSourceHealthConfig(Map<String, DataSource> dataSources,
                                          ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
        super(dataSources, metadataProviders);
    }

    @Override
    protected AbstractHealthIndicator createIndicator(DataSource source) {
        DataSourceHealthIndicator indicator = (DataSourceHealthIndicator) super.createIndicator(source);
        if (!StringUtils.hasText(indicator.getQuery())) {
            indicator.setQuery(defaultQuery);
        }
        return indicator;
    }
}