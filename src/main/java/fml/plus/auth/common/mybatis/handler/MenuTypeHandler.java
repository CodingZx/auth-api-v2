package fml.plus.auth.common.mybatis.handler;
import fml.plus.auth.entity.MenuEntity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuTypeHandler extends BaseTypeHandler<MenuEntity.MenuType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MenuEntity.MenuType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public MenuEntity.MenuType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return MenuEntity.MenuType.of(rs.getString(columnName));
    }

    @Override
    public MenuEntity.MenuType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return MenuEntity.MenuType.of(rs.getString(columnIndex));
    }

    @Override
    public MenuEntity.MenuType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MenuEntity.MenuType.of(cs.getString(columnIndex));
    }
}