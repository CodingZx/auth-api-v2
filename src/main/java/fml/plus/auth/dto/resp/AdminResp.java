package fml.plus.auth.dto.resp;

import fml.plus.auth.common.util.SpringUtils;
import fml.plus.auth.common.util.time.LocalDateTimeUtils;
import fml.plus.auth.entity.AccountEntity;
import fml.plus.auth.service.RoleService;
import lombok.Data;

import java.util.UUID;

@Data
public class AdminResp {
    private UUID id; // 账号ID
    private String userName; // 用户名
    private String realName; // 姓名
    private String createTime; // 创建时间
    private UUID roleId; // 关联角色ID
    private String roleName; // 角色名称
    private boolean status;

    public AdminResp(AccountEntity admin){
        this.id = admin.getId();
        this.userName = admin.getUserName();
        this.realName = admin.getRealName();
        this.createTime = LocalDateTimeUtils.format(admin.getCreateTime());
        this.roleId = admin.getRoleId();
        this.roleName = SpringUtils.getBean(RoleService.class).getRoleName(admin.getRoleId());
        this.status = admin.getStatus();
    }
}
