package com.kl.starter.shardings.config;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.EncryptRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.MasterSlaveRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.ShadowRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.ShardingRuleCondition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AutoConfigureAfter({SpringBootConfiguration.class})
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class})
@ConditionalOnClass({DynamicDataSourceAutoConfiguration.class})
public class ShardingConfig {
    public ShardingConfig() {
    }

    @Bean
    @Conditional({ShardingRuleCondition.class})
    public DynamicDataSourceProvider shardingDynamicDataSourceProvider(@Qualifier("shardingDataSource") DataSource shardingDataSource) {
        return this.getAbstractDataSourceProvider(shardingDataSource);
    }

    @Bean
    @Conditional({MasterSlaveRuleCondition.class})
    public DynamicDataSourceProvider masterSlaveDynamicDataSourceProvider(@Qualifier("masterSlaveDataSource") DataSource masterSlaveDataSource) {
        return this.getAbstractDataSourceProvider(masterSlaveDataSource);
    }

    @Bean
    @Conditional({EncryptRuleCondition.class})
    public DynamicDataSourceProvider encryptDynamicDataSourceProvider(@Qualifier("encryptDataSource") DataSource encryptDataSource) {
        return this.getAbstractDataSourceProvider(encryptDataSource);
    }

    @Bean
    @Conditional({ShadowRuleCondition.class})
    public DynamicDataSourceProvider shadowDynamicDataSourceProvider(@Qualifier("shadowDataSource") DataSource shadowDataSource) {
        return this.getAbstractDataSourceProvider(shadowDataSource);
    }

    private AbstractDataSourceProvider getAbstractDataSourceProvider(final DataSource dataSource) {
        return new AbstractDataSourceProvider() {
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = new HashMap(1);
                dataSourceMap.put("sharding", dataSource);
                return dataSourceMap;
            }
        };
    }

    @Bean
    @Primary
    public DataSource dataSource(DynamicDataSourceProperties properties) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }
}
