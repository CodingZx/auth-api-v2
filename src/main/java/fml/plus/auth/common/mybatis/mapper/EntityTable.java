package fml.plus.auth.common.mybatis.mapper;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(fluent = true)
public class EntityTable {
    protected Class<?> clazz; // Entity类型
    protected String table; // 映射表名
    protected List<EntityColumn> columns; // 映射数据库字段

    protected EntityTable(Class<?> entityClass) {
        this.clazz = entityClass;
    }

    public static EntityTable of(Class<?> entityClass) {
        return new EntityTable(entityClass);
    }

    /**
     * 返回所有列
     * @return 所有列信息
     */
    public List<EntityColumn> columns() {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }
        return columns;
    }


    /**
     * 添加列
     */
    public void addColumn(EntityColumn column) {
        //不重复添加同名的列
        if (!columns().contains(column)) {
            if (column.field().getDeclaringClass() != clazz()) {
                columns().add(0, column);
            } else {
                columns().add(column);
            }
            column.entityTable(this);
        } else {
            //同名列在父类存在时，说明是子类覆盖的，字段的顺序应该更靠前
            EntityColumn existsColumn = columns().remove(columns().indexOf(column));
            columns().add(0, existsColumn);
        }
    }

    /**
     * 所有 insert 用到的字段，当插入列时，必须使用当前方法返回的列
     */
    public List<EntityColumn> insertColumns() {
        return columns().stream().filter(EntityColumn::insertable).collect(Collectors.toList());
    }

    /**
     * 所有 update 用到的字段，当更新列时，必须使用当前方法返回的列
     */
    public List<EntityColumn> updateColumns() {
        return columns().stream().filter(e -> e.updatable() && !e.id()).collect(Collectors.toList());
    }

    /**
     * 返回查询列，当获取查询列时，必须使用当前方法返回的列
     */
    public List<EntityColumn> selectColumns() {
        return columns().stream().filter(EntityColumn::selectable).collect(Collectors.toList());
    }

    /**
     * 所有查询列，形如 column1 AS property1, column2 AS property2, ...
     */
    public String columnAsPropertyList() {
        return selectColumns().stream().map(EntityColumn::columnAsProperty).collect(Collectors.joining(", "));
    }

    /**
     * 返回主键列，不会为空，当根据主键作为条件时，必须使用当前方法返回的列，没有设置主键时，当前方法返回所有列
     */
    public List<EntityColumn> idColumns() {
        List<EntityColumn> idColumns = columns().stream().filter(EntityColumn::id).collect(Collectors.toList());
        if (idColumns.isEmpty()) {
            return columns();
        }
        return idColumns;
    }
}
