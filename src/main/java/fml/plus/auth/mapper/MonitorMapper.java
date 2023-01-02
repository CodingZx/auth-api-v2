package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.MonitorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MonitorMapper extends BaseMapper<UUID, MonitorEntity> {

    @Select("select server from a_monitor group by server")
    List<String> findServers();
}
