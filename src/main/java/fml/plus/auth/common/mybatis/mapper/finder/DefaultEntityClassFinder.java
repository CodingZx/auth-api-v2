package fml.plus.auth.common.mybatis.mapper.finder;


import fml.plus.auth.common.mybatis.mapper.annotation.Table;

/**
 * 默认实现，针对 {@link Table} 注解实现
 */
class DefaultEntityClassFinder extends GenericEntityClassFinder {

  @Override
  public boolean isEntityClass(Class<?> clazz) {
    return clazz.isAnnotationPresent(Table.class);
  }

}
