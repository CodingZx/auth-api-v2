package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.IPLimitEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface IPLimitMapper extends BaseMapper<UUID, IPLimitEntity> {
}
