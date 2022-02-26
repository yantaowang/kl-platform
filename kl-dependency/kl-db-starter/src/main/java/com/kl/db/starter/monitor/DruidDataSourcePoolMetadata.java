package com.kl.db.starter.monitor;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;

/**
 * Druid数据源监控数据收集
 * 参考 {@link HikariDataSourcePoolMetadata}
 *
 * @author xinminghao
 */
public class DruidDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DruidDataSource> {

    /**
     * Create an instance with the data source to use.
     *
     * @param dataSource the data source
     */
    public DruidDataSourcePoolMetadata(DruidDataSource dataSource) {
        super(dataSource);
    }

    /**
     * 正在使用的连接
     */
    @Override
    public Integer getActive() {
        return getDataSource().getActiveCount();
    }

    /**
     * 最大连接
     */
    @Override
    public Integer getMax() {
        return getDataSource().getMaxActive();
    }

    /**
     * 初始化连接
     */
    @Override
    public Integer getMin() {
        return getDataSource().getMinIdle();
    }

    /**
     * 闲置连接
     */
    @Override
    public Integer getIdle() {
        return getDataSource().getPoolingCount();
    }

    /**
     * 验证数据库连接的查询语句
     */
    @Override
    public String getValidationQuery() {
        return getDataSource().getValidationQuery();
    }

    /**
     * 自动提交或回滚
     */
    @Override
    public Boolean getDefaultAutoCommit() {
        return getDataSource().isDefaultAutoCommit();
    }
}
