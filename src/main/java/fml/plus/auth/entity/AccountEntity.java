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
@Table(name = "a_account", remark = "账号信息")
public class AccountEntity {
    @Column(value = "id", id = true)
    private UUID id;
    @Column(value = "user_name", remark = "用户名")
    private String userName;
    @Column(value = "real_name", remark = "姓名")
    private String realName;
    @Column(value = "password", remark = "登录密码")
    private String password;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;
    @Column(value = "role_id", remark = "角色")
    private UUID roleId;
    @Column(value = "status", remark = "状态 true:正常 false:禁用")
    private Boolean status;

}
