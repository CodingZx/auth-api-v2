package fml.plus.auth.common.mybatis.mapper.mapper;

import fml.plus.auth.common.mybatis.mapper.Caching;
import fml.plus.auth.common.mybatis.mapper.provider.Example;
import fml.plus.auth.common.mybatis.mapper.provider.ExampleProvider;
import fml.plus.auth.common.mybatis.mapper.provider.ExampleWrapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Example 相关方法
 * @param <T> 实体类
 */
public interface ExampleMapper<T> {

    /**
     * 获取 Example 对象
     *
     * @return Example 对象
     */
    default Example<T> example() {
        return new Example<>();
    }

    /**
     * Example 查询封装
     */
    default ExampleWrapper<T> wrapper() {
        return new ExampleWrapper<>(ExampleMapper.this);
    }

    /**
     * 根据 Example 条件批量查询
     *
     * @param example 条件
     * @return 实体列表
     */
    @Lang(Caching.class)
    @SelectProvider(type = ExampleProvider.class, method = "findByExample")
    List<T> findList(Example<T> example);

    /**
     * 根据 Example 条件查询单个实体
     *
     * @param example 条件
     * @return 单个实体，查询结果由多条时报错
     */
    @Lang(Caching.class)
    @SelectProvider(type = ExampleProvider.class, method = "findByExample")
    T findOne(Example<T> example);

    /**
     * 根据 Example 条件查询总数
     *
     * @param example 条件
     * @return 总数
     */
    @Lang(Caching.class)
    @SelectProvider(type = ExampleProvider.class, method = "findCountByExample")
    long findCount(Example<T> example);


    /**
     * 根据 Example 条件和 setValue 值更新字段
     *
     * @param example 条件
     * @return 大于等于1成功，0失败
     */
    @Lang(Caching.class)
    @UpdateProvider(type = ExampleProvider.class, method = "updateByExampleSetValues")
    int update(@Param("example") Example<T> example);


    /**
     * 根据 Example 删除
     *
     * @param example 条件
     * @return 大于等于1成功，0失败
     */
    @Lang(Caching.class)
    @DeleteProvider(type = ExampleProvider.class, method = "deleteByExample")
    int delete(Example<T> example);
}