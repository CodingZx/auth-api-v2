package fml.plus.auth.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Data
public class AdminReq {
    private UUID id; // 用户ID
    @NotEmpty(message = "账号不能为空")
    @NotNull(message = "账号不能为空")
    @Length(min = 3, max = 10, message = "账号长度必须在3-10之间")
    private String userName; // 账号
    @NotEmpty(message = "姓名不能为空")
    @NotNull(message = "姓名不能为空")
    private String realName; // 姓名
    private String password; // 密码
    @NotNull(message = "角色ID不能为空")
    private UUID roleId; // 关联角色
}
