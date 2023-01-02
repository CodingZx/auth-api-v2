package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MenuMapper extends BaseMapper<UUID, MenuEntity> {

    default List<MenuEntity> findByParentId(UUID parentId) {
        return wrapper().eq(MenuEntity::getParentId, parentId).list();
    }
}
