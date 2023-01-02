package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.ExceptionLogBackupEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface ExceptionLogBackupMapper extends BaseMapper<UUID, ExceptionLogBackupEntity> {
}
