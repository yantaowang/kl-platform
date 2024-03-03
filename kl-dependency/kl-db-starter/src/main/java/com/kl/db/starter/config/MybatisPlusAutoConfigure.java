package com.kl.db.starter.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.kl.db.starter.handler.CreatorDataScopeSqlHandler;
import com.kl.db.starter.handler.SqlHandler;
import com.kl.db.starter.interceptor.CustomTenantInterceptor;
import com.kl.db.starter.interceptor.DataScopeInnerInterceptor;
import com.kl.db.starter.interceptor.EnableQuerySqlLogInnerInterceptor;
import com.kl.db.starter.properties.DataScopeProperties;
import com.kl.db.starter.properties.MybatisPlusAutoFillProperties;
import com.kl.db.starter.properties.TenantProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * mybatis-plus自动配置
 *
 */
@EnableConfigurationProperties(MybatisPlusAutoFillProperties.class)
public class MybatisPlusAutoConfigure {
    @Autowired
    private TenantLineHandler tenantLineHandler;

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private MybatisPlusAutoFillProperties autoFillProperties;

    @Autowired
    private DataScopeProperties dataScopeProperties;

    @Bean
    @ConditionalOnMissingBean
    public SqlHandler sqlHandler() {
        return new CreatorDataScopeSqlHandler();
    }

    /**
     * MybatisPlus 拦截器-支持分页
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(SqlHandler sqlHandler) {
        MybatisPlusInterceptor mpInterceptor = new MybatisPlusInterceptor();
        boolean enableTenant = tenantProperties.getEnable();
        //是否开启多租户隔离
        if (enableTenant) {
            CustomTenantInterceptor tenantInterceptor = new CustomTenantInterceptor(
                    tenantLineHandler, tenantProperties.getIgnoreSqls());
            mpInterceptor.addInnerInterceptor(tenantInterceptor);
        }
        if (dataScopeProperties.getEnabled()) {
            DataScopeInnerInterceptor dataScopeInnerInterceptor = new DataScopeInnerInterceptor(dataScopeProperties, sqlHandler);
            mpInterceptor.addInnerInterceptor(Boolean.TRUE.equals(dataScopeProperties.getEnabledSqlDebug())
                    ? new EnableQuerySqlLogInnerInterceptor(dataScopeInnerInterceptor) : dataScopeInnerInterceptor);
        }
        return mpInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "kl.mybatis-plus.auto-fill", name = "enabled", havingValue = "true", matchIfMissing = true)
    public MetaObjectHandler metaObjectHandler() {
        return new DateMetaObjectHandler(autoFillProperties);
    }
}
