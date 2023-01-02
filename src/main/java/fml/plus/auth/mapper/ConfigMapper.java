package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.ConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface ConfigMapper extends BaseMapper<UUID, ConfigEntity> {

    /**
     * 根据Key查询
     */
    default ConfigEntity findByKey(String key) {
        return wrapper().eq(ConfigEntity::getConfigKey, key).one();
    }

}
