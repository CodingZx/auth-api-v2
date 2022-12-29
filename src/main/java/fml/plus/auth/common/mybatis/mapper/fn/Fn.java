package fml.plus.auth.common.mybatis.mapper.fn;

import fml.plus.auth.common.mybatis.mapper.EntityColumn;
import fml.plus.auth.common.mybatis.mapper.EntityFactory;
import fml.plus.auth.common.mybatis.mapper.EntityTable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 参考通用 Mapper weekend 实现，用于获取方法引用对应的字段信息
 */
public interface Fn<T, R> extends Function<T, R>, Serializable {
    /**
     * 缓存方法引用和对应的列信息
     */
    Map<Fn<?, ?>, EntityColumn> FN_COLUMN_MAP = new HashMap<>();
    /**
     * 缓存方法引用和对应的字段信息
     */
    Map<Fn<?, ?>, Reflections.ClassField> FN_CLASS_FIELD_MAP = new HashMap<>();

    /**
     * 指定字段集合的虚拟表
     *
     * @param fns 指定字段
     * @return 虚拟表
     */
    @SafeVarargs
    static <E> Fns<E> of(Fn<E, ?>... fns) {
        return new Fns<>(fns);
    }

    /**
     * 包含部分字段的虚拟表
     *
     * @param entityClass 实体类类型
     * @param columnNames 列名信息
     * @return 虚拟表
     */
    static <E> Fns<E> of(Class<E> entityClass, String... columnNames) {
        EntityTable entityTable = EntityFactory.getEntityTable(entityClass);
        Set<String> columnNameSet = Arrays.stream(columnNames).collect(Collectors.toSet());
        List<EntityColumn> columns = entityTable.columns().stream().filter(column -> columnNameSet.contains(column.property())).collect(Collectors.toList());
        return new Fns<>(entityClass, entityTable.table(), columns);
    }

    /**
     * 转换为字段：获取方法引用对应的字段信息
     *
     * @return 方法引用对应的字段信息
     */
    default String toField() {
        return toClassField().getField();
    }

    /**
     * 转换为字段对应的列信息：获取方法引用对应的列信息
     *
     * @return 方法引用对应的列信息
     */
    default String toColumn() {
        return toEntityColumn().column();
    }

    /**
     * 获取字段信息
     *
     * @return 字段名和所在类信息
     */
    default Reflections.ClassField toClassField() {
        if (!FN_CLASS_FIELD_MAP.containsKey(this)) {
            synchronized (this) {
                if (!FN_CLASS_FIELD_MAP.containsKey(this)) {
                    FN_CLASS_FIELD_MAP.put(this, Reflections.fnToFieldName(this));
                }
            }
        }
        return FN_CLASS_FIELD_MAP.get(this);
    }

    /**
     * 转换为字段对应的列信息：获取方法引用对应的列信息
     *
     * @return 方法引用对应的列信息
     */
    default EntityColumn toEntityColumn() {
        if (!FN_COLUMN_MAP.containsKey(this)) {
            synchronized (this) {
                if (!FN_COLUMN_MAP.containsKey(this)) {
                    Reflections.ClassField classField = toClassField();
                    List<EntityColumn> columns = EntityFactory.getEntityTable(classField.getClazz()).columns();
                    EntityColumn entityColumn = columns.stream()
                            // 先区分大小写匹配字段
                            .filter(column -> column.property().equals(classField.getField())).findFirst()
                            // 如果不存在，再忽略大小写进行匹配
                            .orElseGet(() -> columns.stream().filter(column -> column.property().equalsIgnoreCase(classField.getField()))
                                    .findFirst().orElseThrow(() -> new RuntimeException(classField.getField()
                                            + " does not mark database column field annotations, unable to obtain column information")));
                    FN_COLUMN_MAP.put(this, entityColumn);
                }
            }
        }
        return FN_COLUMN_MAP.get(this);
    }

    /**
     * 字段数组，用于获取字段对应的所有字段名和列名，当前对象相当于一个部分字段的虚拟表
     *
     * @param <E> 实体类型
     */
    class Fns<E> extends EntityTable {

        /**
         * 通过直接值设置
         *
         * @param entityClass 实体类类型
         * @param table       表名
         * @param columns     列集合
         */
        private Fns(Class<E> entityClass, String table, List<EntityColumn> columns) {
            super(entityClass);
            this.table = table;
            this.columns = columns;
        }

        /**
         * 通过字段设置
         *
         * @param fns 字段数组
         */
        @SafeVarargs
        private Fns(Fn<E, ?>... fns) {
            super(null);
            this.columns = new ArrayList<>(fns.length);
            for (int i = 0; i < fns.length; i++) {
                this.columns.add(fns[i].toEntityColumn());
                if (i == 0) {
                    EntityTable entityTable = this.columns.get(i).entityTable();
                    this.table = entityTable.table();
                    this.clazz = entityTable.clazz();
                }
            }
        }

        /**
         * 当前表字段是否为空，为空则没有设置任何字段
         */
        public boolean isNotEmpty() {
            return !columns().isEmpty();
        }

    }
}
