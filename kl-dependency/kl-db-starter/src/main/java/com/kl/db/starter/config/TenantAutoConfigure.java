package com.kl.db.starter.config;

import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.kl.common.thread.KlThreadLocal;
import com.kl.db.starter.properties.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 多租户自动配置
 *
 */
@EnableConfigurationProperties(TenantProperties.class)
public class TenantAutoConfigure {
    @Autowired
    private TenantProperties tenantProperties;

    @Bean
    public TenantLineHandler tenantHandler() {
        return new TenantLineHandler() {
            /**
             * 获取租户id
             */
            @Override
            public Expression getTenantId() {
                Integer tenant = KlThreadLocal.getPartnerCode();
                if (tenant != null) {
                    return new StringValue(tenant.toString());
                }
                return new NullValue();
            }

            /**
             * 获取租户列名
             */
            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            /**
             * 过滤不需要根据租户隔离的表
             * @param tableName 表名
             */
            @Override
            public boolean ignoreTable(String tableName) {
                return tenantProperties.getIgnoreTables().stream().anyMatch(
                        (e) -> e.equalsIgnoreCase(tableName)
                );
            }
        };
    }

    /**
     * 过滤不需要根据租户隔离的MappedStatement
     */
//    @Bean
//    public ISqlParserFilter sqlParserFilter() {
//        return metaObject -> {
//            MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
//            return tenantProperties.getIgnoreSqls().stream().anyMatch(
//                    (e) -> e.equalsIgnoreCase(ms.getId())
//            );
//        };
//    }
}
