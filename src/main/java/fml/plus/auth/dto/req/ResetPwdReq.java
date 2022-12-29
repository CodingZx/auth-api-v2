package fml.plus.auth.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ResetPwdReq {
    @NotNull(message = "账号ID不能为空")
    private UUID adminId;
    @NotEmpty(message = "密码不能为空")
    private String password; // 密码
}
