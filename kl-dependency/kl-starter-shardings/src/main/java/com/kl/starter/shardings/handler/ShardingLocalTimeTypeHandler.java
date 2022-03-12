package com.kl.starter.shardings.handler;


import cn.hutool.core.convert.Convert;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

@MappedTypes({LocalTime.class})
@MappedJdbcTypes(
        value = {},
        includeNullJdbcType = true
)
public class ShardingLocalTimeTypeHandler extends BaseTypeHandler<LocalTime> {
    public ShardingLocalTimeTypeHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, LocalTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    public LocalTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        return object == null ? null : Convert.toLocalDateTime(object).toLocalTime();
    }

    public LocalTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object object = rs.getObject(columnIndex);
        return object == null ? null : Convert.toLocalDateTime(object).toLocalTime();
    }

    public LocalTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object object = cs.getObject(columnIndex);
        return object == null ? null : Convert.toLocalDateTime(object).toLocalTime();
    }
}
