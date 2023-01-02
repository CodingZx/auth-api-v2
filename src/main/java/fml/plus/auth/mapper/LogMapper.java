package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface LogMapper extends BaseMapper<UUID, LogEntity> {
}
