package fml.plus.auth.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoleReq {
    private UUID id; // 角色ID
    @NotEmpty(message = "角色名称不能为空")
    private String roleName; // 角色名称
    @NotEmpty(message = "请选择菜单")
    private List<UUID> menus; // 选择的菜单ID
}
