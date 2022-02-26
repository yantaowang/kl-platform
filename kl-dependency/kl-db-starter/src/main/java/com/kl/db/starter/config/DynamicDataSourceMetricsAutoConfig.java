package com.kl.db.starter.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.kl.db.starter.monitor.MicrometerGaugeUtils;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration;
import org.springframework.boot.actuate.metrics.jdbc.DataSourcePoolMetrics;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DynamicDatasource 中 Druid 数据源监控数据收集
 * 参考 {@link DataSourcePoolMetricsAutoConfiguration}
 *
 * @author xinminghao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({
        DynamicRoutingDataSource.class,
        DataSource.class,
        MeterRegistry.class,
})
@ConditionalOnBean({
        DataSource.class,
        MeterRegistry.class,
})
@AutoConfigureAfter({
        MetricsAutoConfiguration.class,
        DynamicDataSourceAutoConfiguration.class,
        SimpleMetricsExportAutoConfiguration.class,
        DataSourcePoolMetricsAutoConfiguration.class,
        DruidDataSourceMetricsAutoConfig.class,
})
public class DynamicDataSourceMetricsAutoConfig {

    private final static String DATASOURCE_NAME_PRE = "dynamic-";

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({
            DruidDataSource.class,
    })
    @ConditionalOnBean(DataSourcePoolMetadataProvider.class)
    static class DruidDataSourcePoolMetadataMetricsConfiguration {
        /**
         * 绑定数据源数据监听
         *
         * @param dataSources       注册成Bean的数据源Map
         * @param registry          监听数据注册器
         * @param metadataProviders 数据收集器集合
         */
        @Autowired
        void bindMetricsRegistryToDynamicDruidDataSources(Collection<DynamicRoutingDataSource> dataSources,
                                                          MeterRegistry registry,
                                                          ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
            List<DataSourcePoolMetadataProvider> metadataProvidersList = metadataProviders.stream()
                    .collect(Collectors.toList());
            for (DynamicRoutingDataSource dataSource : dataSources) {
                Map<String, DataSource> dynamicRoutingDataSourceMap = dataSource.getCurrentDataSources();

                for (String dynamicRoutingDataSourceName : dynamicRoutingDataSourceMap.keySet()) {
                    DataSource dataSourceInDynamicRouting = dynamicRoutingDataSourceMap.get(dynamicRoutingDataSourceName);
                    // druid
                    DruidDataSource druidDataSource = DataSourceUnwrapper.unwrap(dataSourceInDynamicRouting,
                            DruidDataSource.class);
                    if (druidDataSource != null) {
                        bindMetricsRegistryToDruidDataSource(DATASOURCE_NAME_PRE + dynamicRoutingDataSourceName,
                                druidDataSource, metadataProvidersList, registry);
                    }
                }
            }
        }

        private void bindMetricsRegistryToDruidDataSource(String dataSourceName, DruidDataSource druidDataSource,
                                                          Collection<DataSourcePoolMetadataProvider> metadataProviders,
                                                          MeterRegistry registry) {

            if (!MicrometerGaugeUtils.isDataSourceRegister(dataSourceName)) {
                try {
                    MicrometerGaugeUtils.registerDruidGauge(registry, dataSourceName, druidDataSource);
                } catch (Exception ex) {
                    log.error("Failed to bind Druid metrics tracker for " + dataSourceName + ":", ex);
                }
            }

            try {
                new DataSourcePoolMetrics(druidDataSource, metadataProviders, dataSourceName, Collections.emptyList())
                        .bindTo(registry);
            } catch (Exception ex) {
                log.error("Failed to bind Druid metrics for " + dataSourceName + ":", ex);
            }
        }
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({
            HikariDataSource.class,
    })
    @ConditionalOnBean(DataSourcePoolMetadataProvider.class)
    static class HikariDataSourcePoolMetadataMetricsConfiguration {

        /**
         * 绑定数据源数据监听
         *
         * @param dataSources       注册成Bean的数据源Map
         * @param registry          监听数据注册器
         * @param metadataProviders 数据收集器集合
         */
        @Autowired
        void bindMetricsRegistryToDynamicHikariDataSources(Collection<DynamicRoutingDataSource> dataSources,
                                                           MeterRegistry registry,
                                                           ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
            List<DataSourcePoolMetadataProvider> metadataProvidersList = metadataProviders.stream()
                    .collect(Collectors.toList());
            for (DynamicRoutingDataSource dynamicRoutingDataSource : dataSources) {
                Map<String, DataSource> dynamicRoutingDataSourceMap = dynamicRoutingDataSource.getCurrentDataSources();

                for (String dynamicRoutingDataSourceName : dynamicRoutingDataSourceMap.keySet()) {
                    DataSource dataSourceInDynamicRouting = dynamicRoutingDataSourceMap.get(dynamicRoutingDataSourceName);
                    // hikari
                    HikariDataSource hikariDataSource = DataSourceUnwrapper.unwrap(dataSourceInDynamicRouting,
                            HikariDataSource.class);
                    if (hikariDataSource != null) {
                        bindMetricsRegistryToHikariDataSource(DATASOURCE_NAME_PRE + dynamicRoutingDataSourceName,
                                hikariDataSource, metadataProvidersList, registry);
                    }
                }
            }
        }

        private void bindMetricsRegistryToHikariDataSource(String name, HikariDataSource hikari,
                                                           Collection<DataSourcePoolMetadataProvider> metadataProviders,
                                                           MeterRegistry registry) {
            if (hikari.getMetricRegistry() == null && hikari.getMetricsTrackerFactory() == null) {
                try {
                    hikari.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(registry) {
                        @Override
                        public IMetricsTracker create(String poolName, PoolStats poolStats) {
                            return super.create(DATASOURCE_NAME_PRE + poolName, poolStats);
                        }
                    });
                } catch (Exception ex) {
                    log.error("Failed to bind Hikari metrics tracker for " + name + ":", ex);
                }

                try {
                    bindDataSourceToRegistry(name, hikari, metadataProviders, registry);
                } catch (Exception ex) {
                    log.error("Failed to bind Hikari metrics for " + name + ":", ex);
                }
            }
        }

        private void bindDataSourceToRegistry(String dataSourceName, DataSource dataSource,
                                              Collection<DataSourcePoolMetadataProvider> metadataProviders,
                                              MeterRegistry registry) {
            new DataSourcePoolMetrics(dataSource, metadataProviders, dataSourceName, Collections.emptyList())
                    .bindTo(registry);
        }
    }
}
