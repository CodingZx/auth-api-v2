package fml.plus.auth.common.mybatis.mapper.provider;

import fml.plus.auth.common.mybatis.mapper.EntityColumn;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.stream.Collectors;

import static fml.plus.auth.common.mybatis.mapper.provider.SqlScript.*;

public class EntityProvider {

    /**
     * 保存实体
     */
    public static String insert(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            // 生成COLUMN SQL
            LRSupplier columns = () -> entity.insertColumns().stream().map(EntityColumn::column).collect(Collectors.joining(", "));
            // 生成VALUES SQL
            LRSupplier values = () -> entity.insertColumns().stream().map(EntityColumn::variables).collect(Collectors.joining(", "));

            return INSERT(entity.table(), columns, values);
        });
    }

    /**
     * 保存实体中不为空的字段
     */
    public static String insertSelective(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            // 生成COLUMN SQL
            LRSupplier columns = () -> trimSuffixOverrides("","", ",", () -> entity.insertColumns().stream().map(column -> ifTest(column.notNullTest(), () -> column.column() + ",")).collect(Collectors.joining(LF)));
            // 生成VALUES SQL
            LRSupplier values = () -> trimSuffixOverrides("", "", ",", () -> entity.insertColumns().stream().map(column -> ifTest(column.notNullTest(), () -> column.variables() + ",")).collect(Collectors.joining(LF)));

            return script(() -> INSERT(entity.table(), columns, values));
        });
    }


    /**
     * 根据主键更新实体
     */
    public static String updateById(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            // 生成 SET SQL
            LRSupplier sets = () -> entity.updateColumns().stream().map(EntityColumn::columnEquals).collect(Collectors.joining(", "));
            // 生成 WHERE SQL
            LRSupplier where = () -> entity.idColumns().stream().map(EntityColumn::columnEquals).collect(Collectors.joining(" AND "));

            return script(() -> UPDATE(entity.table(), sets, where));
        });
    }

    /**
     * 根据主键更新实体中不为空的字段
     */
    public static String updateSelectiveById(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            // 生成 SET SQL
            LRSupplier sets = () -> entity.updateColumns().stream().map(column -> ifTest(column.notNullTest(), () -> column.columnEquals() + ",")).collect(Collectors.joining(LF));
            // 生成 WHERE SQL
            LRSupplier where = () -> entity.idColumns().stream().map(EntityColumn::columnEquals).collect(Collectors.joining(" AND "));

            return script(() -> UPDATE(entity.table(), sets, where));
        });
    }

    /**
     * 根据主键删除
     */
    public static String deleteById(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            if(context.getMapperMethod().getParameterCount() != entity.idColumns().size()) {
                throw new IllegalArgumentException("id count does not match...");
            }

            return script(() -> DELETE(entity.table(), () -> entity.idColumns().get(0).columnEquals()));
        });
    }


    /**
     * 根据主键查询实体
     */
    public static String findById(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            if(context.getMapperMethod().getParameterCount() != entity.idColumns().size()) {
                throw new IllegalArgumentException("id count does not match...");
            }
            // 生成 WHERE SQL
            LRSupplier where = () -> entity.idColumns().stream().map(EntityColumn::columnEquals).collect(Collectors.joining(" AND "));

            return script(() -> SELECT(entity.table(), entity::columnAsPropertyList, where));
        });
    }
}
