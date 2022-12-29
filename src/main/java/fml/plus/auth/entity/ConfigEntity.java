package fml.plus.auth.entity;

import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "a_config", remark = "配置信息")
public class ConfigEntity {

    @Column(value = "id", id = true)
    private UUID id;
    @Column(value = "config_key", remark = "配置Key")
    private String configKey;
    @Column(value = "values", remark = "配置内容")
    private String values;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;

}
