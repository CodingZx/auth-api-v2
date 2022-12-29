package fml.plus.auth.dto.resp;

import fml.plus.auth.entity.RoleEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RoleResp {
    private UUID id; // 角色ID
    private String roleName; // 角色名称
    private LocalDateTime createTime; // 创建时间

    public RoleResp(RoleEntity roleEntity){
        this.id = roleEntity.getId();
        this.roleName = roleEntity.getRoleName();
        this.createTime = roleEntity.getCreateTime();
    }
}
