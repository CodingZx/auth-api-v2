package fml.plus.auth.common.mybatis.mapper;

import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import fml.plus.auth.common.mybatis.mapper.finder.EntityClassFinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityFactory {
    private static final Map<Class<?>, EntityTable> ENTITY_CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * 获取类型对应的实体信息
     *
     * @param mapperType   接口
     * @param mapperMethod 方法
     * @return 实体类信息
     */
    public static EntityTable getEntityTable(Class<?> mapperType, Method mapperMethod) {
        Optional<Class<?>> optionalClass = EntityClassFinder.find(mapperType, mapperMethod);
        if (optionalClass.isPresent()) {
            return getEntityTable(optionalClass.get());
        }
        throw new IllegalArgumentException("Can't obtain " + (mapperMethod != null ?
                mapperMethod.getName() + " method" : mapperType.getSimpleName() + " interface") + " corresponding entity class...");
    }

    /**
     * 获得EntityTable信息
     */
    public static EntityTable getEntityTable(Class<?> entity) {
        var entityTable = ENTITY_CLASS_MAP.get(entity);
        if (entityTable == null) {
            // 构建Entity信息
            synchronized (entity) {
                entityTable = build(entity);
                ENTITY_CLASS_MAP.put(entity, entityTable);
            }
        }
        return entityTable;
    }

    /**
     * 构建EntityTable信息
     */
    private static EntityTable build(Class<?> entity) {
        var table = entity.getAnnotation(Table.class);
        EntityTable entityTable = EntityTable.of(entity)
                .table(table.name().isEmpty() ? entity.getSimpleName() : table.name());
        Class<?> declaredClass = entity;
        while (declaredClass != null && declaredClass != Object.class) {
            Field[] declaredFields = declaredClass.getDeclaredFields();
            for (Field field : declaredFields) {
                int modifiers = field.getModifiers();
                //排除 static 和 transient 修饰的字段
                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                    var columnAnno = field.getAnnotation(Column.class);
                    if(columnAnno == null) continue;
                    entityTable.addColumn(new EntityColumn(field, columnAnno));
                }
            }
            //迭代获取父类
            declaredClass = declaredClass.getSuperclass();
        }
        return entityTable;
    }
}
