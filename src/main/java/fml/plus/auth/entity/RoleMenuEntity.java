package fml.plus.auth.entity;

import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "a_role_menu", remark = "角色菜单关联信息")
public class RoleMenuEntity {

    @Column(value = "id", id = true)
    private UUID id; // ID
    @Column(value = "role_id", remark = "角色ID")
    private UUID roleId;
    @Column(value = "menu_id", remark = "菜单ID")
    private UUID menuId;
}
