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
@Table(name = "a_role", remark = "角色信息")
public class RoleEntity {

    @Column(value = "id", id = true)
    private UUID id; // ID
    @Column(value = "role_name", remark = "角色名称")
    private String roleName;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;
}