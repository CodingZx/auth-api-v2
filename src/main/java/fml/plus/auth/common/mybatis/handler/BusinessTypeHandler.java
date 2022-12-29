package fml.plus.auth.common.mybatis.handler;

import fml.plus.auth.common.aop.log.BusinessType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BusinessTypeHandler extends BaseTypeHandler<BusinessType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BusinessType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getType());
    }

    @Override
    public BusinessType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return BusinessType.of(rs.getInt(columnName));
    }

    @Override
    public BusinessType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return BusinessType.of(rs.getInt(columnIndex));
    }

    @Override
    public BusinessType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return BusinessType.of(cs.getInt(columnIndex));
    }
}

