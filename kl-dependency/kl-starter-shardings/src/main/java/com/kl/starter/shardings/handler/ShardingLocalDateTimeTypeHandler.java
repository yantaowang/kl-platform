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
import java.time.LocalDateTime;

@MappedTypes({LocalDateTime.class})
@MappedJdbcTypes(
        value = {},
        includeNullJdbcType = true
)
public class ShardingLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {
    public ShardingLocalDateTimeTypeHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Convert.toLocalDateTime(rs.getObject(columnName));
    }

    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Convert.toLocalDateTime(rs.getObject(columnIndex));
    }

    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Convert.toLocalDateTime(cs.getObject(columnIndex));
    }
}
