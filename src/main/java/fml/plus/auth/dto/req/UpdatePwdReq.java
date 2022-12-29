package fml.plus.auth.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdatePwdReq {
    @NotEmpty(message = "当前密码不能为空")
    private String oldPassword; // 密码
    @NotEmpty(message = "新密码不能为空")
    @Length(min = 3, max = 20, message = "密码长度必须在3-20之间")
    private String newPassword; // 密码
}
