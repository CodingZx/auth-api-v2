package fml.plus.auth.common.mybatis.handler;

import fml.plus.auth.entity.ExceptionLogEntity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExceptionLogStatusHandler extends BaseTypeHandler<ExceptionLogEntity.ExceptionLogStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExceptionLogEntity.ExceptionLogStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public ExceptionLogEntity.ExceptionLogStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ExceptionLogEntity.ExceptionLogStatus.of(rs.getInt(columnName));
    }

    @Override
    public ExceptionLogEntity.ExceptionLogStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ExceptionLogEntity.ExceptionLogStatus.of(rs.getInt(columnIndex));
    }

    @Override
    public ExceptionLogEntity.ExceptionLogStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ExceptionLogEntity.ExceptionLogStatus.of(cs.getInt(columnIndex));
    }
}
