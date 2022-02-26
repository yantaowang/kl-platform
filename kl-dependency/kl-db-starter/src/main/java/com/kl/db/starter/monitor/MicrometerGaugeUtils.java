package com.kl.db.starter.monitor;


import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToDoubleFunction;

public class MicrometerGaugeUtils {

    private final static String LABEL_NAME = "pool";
    private final static CopyOnWriteArrayList<String> registerDataSources = new CopyOnWriteArrayList<>();

    public static boolean isDataSourceRegister(String dataSourceName) {
        return registerDataSources.contains(dataSourceName);
    }

    public static void registerDruidGauge(MeterRegistry registry, String dataSourceName, DruidDataSource druidDataSource) {
        registerDataSources.add(dataSourceName);
        try {
            // connection pool core metrics
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_active_peak",
                    "Active peak", datasource -> (double) druidDataSource.getActivePeak());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_pooling_peak",
                    "Pooling peak", datasource -> (double) druidDataSource.getPoolingPeak());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_wait_thread_count",
                    "Wait thread count", datasource -> (double) druidDataSource.getWaitThreadCount());

            // connection pool detail metrics
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_not_empty_wait_count",
                    "Not empty wait count", datasource -> (double) druidDataSource.getNotEmptyWaitCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_not_empty_wait_millis",
                    "Not empty wait millis", datasource -> (double) druidDataSource.getNotEmptyWaitMillis());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_not_empty_thread_count",
                    "Not empty thread count", datasource -> (double) druidDataSource.getNotEmptyWaitThreadCount());

            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_logic_connect_count",
                    "Logic connect count", datasource -> (double) druidDataSource.getConnectCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_logic_close_count",
                    "Logic close count", datasource -> (double) druidDataSource.getCloseCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_logic_connect_error_count",
                    "Logic connect error count", datasource -> (double) druidDataSource.getConnectErrorCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_physical_connect_count",
                    "Physical connect count", datasource -> (double) druidDataSource.getCreateCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_physical_close_count",
                    "Physical close count", datasource -> (double) druidDataSource.getDestroyCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_physical_connect_error_count",
                    "Physical connect error count", datasource -> (double) druidDataSource.getCreateErrorCount());

            // sql execution core metrics
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_error_count",
                    "Error count", datasource -> (double) druidDataSource.getErrorCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_execute_count",
                    "Execute count", datasource -> (double) druidDataSource.getExecuteCount());
            // transaction metrics
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_start_transaction_count",
                    "Start transaction count", datasource -> (double) druidDataSource.getStartTransactionCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_commit_count",
                    "Commit count", datasource -> (double) druidDataSource.getCommitCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_rollback_count",
                    "Rollback count", datasource -> (double) druidDataSource.getRollbackCount());

            // sql execution detail
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_prepared_statement_open_count",
                    "Prepared statement open count", datasource -> (double) druidDataSource.getPreparedStatementCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_prepared_statement_closed_count",
                    "Prepared statement closed count", datasource -> (double) druidDataSource.getClosedPreparedStatementCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_ps_cache_access_count",
                    "PS cache access count", datasource -> (double) druidDataSource.getCachedPreparedStatementAccessCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_ps_cache_hit_count",
                    "PS cache hit count", datasource -> (double) druidDataSource.getCachedPreparedStatementHitCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_ps_cache_miss_count",
                    "PS cache miss count", datasource -> (double) druidDataSource.getCachedPreparedStatementMissCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_execute_query_count",
                    "Execute query count", datasource -> (double) druidDataSource.getExecuteQueryCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_execute_update_count",
                    "Execute update count", datasource -> (double) druidDataSource.getExecuteUpdateCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_execute_batch_count",
                    "Execute batch count", datasource -> (double) druidDataSource.getExecuteBatchCount());

            // none core metrics, some are static configurations
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_max_wait",
                    "Max wait", datasource -> (double) druidDataSource.getMaxWait());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_max_wait_thread_count",
                    "Max wait thread count", datasource -> (double) druidDataSource.getMaxWaitThreadCount());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_login_timeout",
                    "Login timeout", datasource -> (double) druidDataSource.getLoginTimeout());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_query_timeout",
                    "Query timeout", datasource -> (double) druidDataSource.getQueryTimeout());
            registerDataSourceGaugeItem(registry, druidDataSource, dataSourceName, "druid_transaction_query_timeout",
                    "Transaction query timeout", datasource -> (double) druidDataSource.getTransactionQueryTimeout());
        } catch (Exception e) {
            registerDataSources.remove(dataSourceName);
            throw e;
        }
    }

    /**
     * 注册数据源Gauge
     */
    private static void registerDataSourceGaugeItem(MeterRegistry registry, DruidDataSource dataSource, String dataSourceName, String metric, String description,
                                                    ToDoubleFunction<DruidDataSource> measure) {
        Gauge.builder(metric, dataSource, measure)
                .description(description)
                .tag(LABEL_NAME, dataSourceName)
                .register(registry);
    }
}
