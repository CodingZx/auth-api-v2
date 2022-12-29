package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.ExceptionLogEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ExceptionLogMapper extends BaseMapper<UUID, ExceptionLogEntity> {

    @Update("""
            update a_exception_log
            set counter = counter + 1, last_report_time = #{lastTime}
            where id = #{id}
            """)
    void updateCounter(@Param("id")UUID id, @Param("lastTime")LocalDateTime lastTime);
}
