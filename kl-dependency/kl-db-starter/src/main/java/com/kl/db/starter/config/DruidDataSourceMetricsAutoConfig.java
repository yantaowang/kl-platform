package com.kl.db.starter.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.kl.db.starter.monitor.DruidDataSourcePoolMetadata;
import com.kl.db.starter.monitor.MicrometerGaugeUtils;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Druid数据源监控配置
 * 参考 {@link DataSourcePoolMetadataProvidersConfiguration}
 * 参考 {@link DataSourcePoolMetricsAutoConfiguration}
 *
 * @author xinminghao
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({
        DataSource.class,
        MeterRegistry.class,
        DruidDataSource.class,
})
@AutoConfigureBefore(DataSourcePoolMetricsAutoConfiguration.class)
public class DruidDataSourceMetricsAutoConfig {

    /**
     * Spring Boot jdbc 监控配置
     */
    @Bean
    public DataSourcePoolMetadataProvider druidPoolDataSourceMetadataProvider() {
        return (dataSource) -> {
            DruidDataSource ds = DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
            if (ds != null) {
                return new DruidDataSourcePoolMetadata(ds);
            }
            return null;
        };
    }

    /**
     * Druid其他信息监控配置
     */
    @Slf4j
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({
            DruidDataSource.class,
    })
    static class DruidDataSourcePoolMetadataMetricsConfiguration {

        private static final String DATASOURCE_SUFFIX = "dataSource";

        /**
         * 绑定数据源数据监听
         *
         * @param dataSources 注册成Bean的数据源Map
         * @param registry    监听数据注册器
         */
        @Autowired
        void bindMetricsRegistryToDynamicDruidDataSources(Map<String, DataSource> dataSources,
                                                          MeterRegistry registry) {
            for (String beanName : dataSources.keySet()) {
                DataSource dataSource = dataSources.get(beanName);
                DruidDataSource ds = DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
                if (ds != null) {
                    String dataSourceName = this.getDataSourceName(beanName);
                    if (!MicrometerGaugeUtils.isDataSourceRegister(dataSourceName)) {
                        try {
                            MicrometerGaugeUtils.registerDruidGauge(registry, dataSourceName, ds);
                        } catch (Exception ex) {
                            log.error("Failed to bind Druid metrics tracker for " + dataSourceName + ":", ex);
                        }
                    }
                }
            }
        }

        /**
         * Get the name of a DataSource based on its {@code beanName}.
         *
         * @param beanName the name of the data source bean
         * @return a name for the given data source
         */
        private String getDataSourceName(String beanName) {
            if (beanName.length() > DATASOURCE_SUFFIX.length()
                    && StringUtils.endsWithIgnoreCase(beanName, DATASOURCE_SUFFIX)) {
                return beanName.substring(0, beanName.length() - DATASOURCE_SUFFIX.length());
            }
            return beanName;
        }
    }
}
