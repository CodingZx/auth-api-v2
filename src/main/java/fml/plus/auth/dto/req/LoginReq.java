package fml.plus.auth.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginReq {
    @NotEmpty(message = "用户名不能为空")
    private String userName; // 用户名
    @NotEmpty(message = "密码不能为空")
    private String password; // 密码
}
