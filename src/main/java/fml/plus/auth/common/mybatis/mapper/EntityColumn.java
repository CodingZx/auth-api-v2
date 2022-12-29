package fml.plus.auth.common.mybatis.mapper;

import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Accessors(fluent = true)
public class EntityColumn {
    public static final Pattern DELIMITER         = Pattern.compile("^[`\\[\"]?(.*?)[`\\]\"]?$");

    protected EntityTable entityTable; // 所在实体类
    private Field field; // 对应字段
    private Type fieldType; // 字段类型
    private String fieldName; // 字段名称
    private String column; // 映射数据库字段名称
    private boolean id; // 是否为主键

    private boolean insertable; // 是否为插入字段
    private boolean updatable; // 是否为修改字段
    private boolean selectable; // 是否为查询字段
    private JdbcType jdbcType; // JDBC类型
    private Class<? extends TypeHandler<?>> typeHandler; //typeHandler

    public EntityColumn(Field field, Column column) {
        this.field = field;
        this.fieldName = field.getName();
        this.fieldType = field.getGenericType();
        this.column = column.value();
        this.id = column.id();
        this.insertable = column.insertable();
        this.updatable = column.updatable();
        this.selectable = column.selectable();
        this.jdbcType = column.jdbcType();
        this.typeHandler = column.typeHandler();
    }

    /**
     * 返回 xml 变量形式 #{property}
     */
    public String variables() {
        return variables("");
    }

    /**
     * 返回带前缀的 xml 变量形式 #{prefixProperty}
     * @param prefix 指定前缀，需要自己提供"."
     */
    public String variables(String prefix) {
        return "#{" + property(prefix)
                + jdbcTypeVariables().orElse("")
                + typeHandlerVariables().orElse("")
                + "}";
    }
    /**
     * 数据库类型 {, jdbcType=VARCHAR}
     */
    public Optional<String> jdbcTypeVariables() {
        if (this.jdbcType != null && this.jdbcType != JdbcType.UNDEFINED) {
            return Optional.of(", jdbcType=" + jdbcType);
        }
        return Optional.empty();
    }

    /**
     * 类型处理器 {, typeHandler=XXTypeHandler}
     */
    public Optional<String> typeHandlerVariables() {
        if (this.typeHandler != null && this.typeHandler != UnknownTypeHandler.class) {
            return Optional.of(", typeHandler=" + typeHandler.getName());
        }
        return Optional.empty();
    }
    /**
     * 带指定前缀的属性名
     * @param prefix 指定前缀，需要自己提供"."
     */
    public String property(String prefix) {
        return prefix + fieldName;
    }

    /**
     * 属性名
     */
    public String property() {
        return property("");
    }


    /**
     * 返回 property != null 形式的字符串
     */
    public String notNullTest() {
        return notNullTest("");
    }

    /**
     * 返回带前缀的  prefixProperty != null 形式的字符串
     *
     * @param prefix 指定前缀，需要自己提供"."
     */
    public String notNullTest(String prefix) {
        return property(prefix) + " != null";
    }

    /**
     * 当字段类型为 String 时，返回 property != null and property != '' 形式的字符串.
     * 其他类型时和 {@link #notNullTest()} 方法一样.
     */
    public String notEmptyTest() {
        return notEmptyTest("");
    }

    /**
     * 当字段类型为 String 时，返回 prefixproperty != null and prefixproperty != '' 形式的字符串.
     * 其他类型时和 {@link #notNullTest()} 方法一样.
     *
     * @param prefix 指定前缀，需要自己提供"."
     */
    public String notEmptyTest(String prefix) {
        if (fieldType.equals(String.class)) {
            return notNullTest(prefix) + " and " + property(prefix) + " != '' ";
        }
        return notNullTest();
    }

    /**
     * 返回 column = #{property} 形式的字符串
     */
    public String columnEquals() {
        return columnEquals("");
    }

    /**
     * 返回带前缀的 column = #{prefixProperty} 形式的字符串
     *
     * @param prefix 指定前缀，需要自己提供"."
     */
    public String columnEquals(String prefix) {
        return column() + " = " + variables(prefix);
    }


    /**
     * 返回 column AS property 形式的字符串, 当 column 和 property 相同时没有别名
     */
    public String columnAsProperty() {
        return columnAsProperty("");
    }

    /**
     * 返回 column AS prefixProperty 形式的字符串
     *
     * @param prefix 指定前缀，需要自己提供"."
     */
    public String columnAsProperty(String prefix) {
        String column = column();
        Matcher matcher = DELIMITER.matcher(column());
        if (matcher.find()) {
            column = matcher.group(1);
        }
        if (!Objects.equals(column, property(prefix))) {
            return column + " AS " + property(prefix);
        }
        return column;
    }

}
