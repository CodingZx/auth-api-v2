package fml.plus.auth.common.mybatis.mapper.finder;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 根据类型和方法等信息获取实体类类型
 */
public interface EntityClassFinder {

  /**
   * 查找当前方法对应的实体类
   *
   * @param mapperType   Mapper 接口，不能为空
   * @param mapperMethod Mapper 接口方法，可以为空
   */
  static Optional<Class<?>> find(Class<?> mapperType, Method mapperMethod) {
    Objects.requireNonNull(mapperType);
    for (EntityClassFinder instance : EntityClassFinderInstance.getInstances()) {
      Optional<Class<?>> optionalClass = instance.findEntityClass(mapperType, mapperMethod);
      if (optionalClass.isPresent()) {
        return optionalClass;
      }
    }
    return Optional.empty();
  }

  /**
   * 查找当前方法对应的实体类
   *
   * @param mapperType   Mapper 接口，不能为空
   * @param mapperMethod Mapper 接口方法，可以为空
   * @return 实体类类型
   */
  Optional<Class<?>> findEntityClass(Class<?> mapperType, Method mapperMethod);

  /**
   * 指定的类型是否为定义的实体类类型
   * @param clazz 类型
   * @return 是否为实体类类型
   */
  boolean isEntityClass(Class<?> clazz);

  /**
   * 实例
   */
  class EntityClassFinderInstance {
    private static final List<EntityClassFinder> INSTANCES = new LinkedList<>();

    static {
      INSTANCES.add(new DefaultEntityClassFinder());
    }
    /**
     * 获取默认实现
     */
    public static List<EntityClassFinder> getInstances() {
      return INSTANCES;
    }
  }

}
