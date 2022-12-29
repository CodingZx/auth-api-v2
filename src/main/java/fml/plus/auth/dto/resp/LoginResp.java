package fml.plus.auth.dto.resp;

import lombok.Data;

import java.util.List;

@Data
public class LoginResp {
    private String token; // 登录凭证Token, 放在Header的X-Access-Token中

    private List<String> permissions; // 拥有的权限
}
