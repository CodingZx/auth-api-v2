package fml.plus.auth.common.mybatis.mapper.mapper;


import fml.plus.auth.common.mybatis.mapper.Caching;
import fml.plus.auth.common.mybatis.mapper.provider.EntityProvider;
import org.apache.ibatis.annotations.*;

public interface EntityMapper<I, E> {

    /**
     * 保存实体
     */
    @Lang(Caching.class)
    @InsertProvider(type = EntityProvider.class, method = "insert")
    int insert(E entity);

    /**
     * 保存实体中不为空的字段
     */
    @Lang(Caching.class)
    @InsertProvider(type = EntityProvider.class, method = "insertSelective")
    int insertSelective(E entity);

    /**
     * 更新实体
     */
    @Lang(Caching.class)
    @UpdateProvider(type = EntityProvider.class, method = "updateById")
    int updateById(E entity);

    /**
     * 根据主键更新实体中不为空的字段
     */
    @Lang(Caching.class)
    @UpdateProvider(type = EntityProvider.class, method = "updateSelectiveById")
    int updateSelectiveById(E entity);

    /**
     * 根据主键删除, 只支持单主键, 联合主键请勿使用此方法
     */
    @Lang(Caching.class)
    @DeleteProvider(type = EntityProvider.class, method = "deleteById")
    int deleteById(I id);

    /**
     * 根据主键查询实体, 只支持单主键, 联合主键请勿使用此方法
     */
    @Lang(Caching.class)
    @SelectProvider(type = EntityProvider.class, method = "findById")
    E findById(I id);
}
