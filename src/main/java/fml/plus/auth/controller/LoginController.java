package fml.plus.auth.controller;

import fml.plus.auth.common.annotation.VisitorAccess;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.model.R;
import fml.plus.auth.common.util.IPUtils;
import fml.plus.auth.dto.req.LoginReq;
import fml.plus.auth.dto.req.UpdatePwdReq;
import fml.plus.auth.dto.resp.CurrentMenuResp;
import fml.plus.auth.dto.resp.LoginResp;
import fml.plus.auth.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class LoginController {
    private LoginService loginService;

    @VisitorAccess
    @GetMapping("/key")
    public R<String> getPublicKey() {
        return R.ok(loginService.getKey());
    }

    @VisitorAccess
    @PostMapping("/login")
    @Log(title = "账号登录", businessType = BusinessType.SELECT, saveRequestData = false)
    public R<LoginResp> login(@RequestBody @Validated LoginReq login, HttpServletRequest request){
        var ip = IPUtils.getRealIp(request);
        login.setUserName(login.getUserName().trim());
        login.setPassword(login.getPassword().trim());
        return R.ok(loginService.login(login.getUserName(), login.getPassword(), ip));
    }

    @GetMapping("/current/menus")
    public R<List<CurrentMenuResp>> menu() {
        var roleId = UserThreadInfo.get().getRoleId();
        return R.ok(loginService.getCurrentMenu(roleId));
    }

    @PutMapping("/update/pwd")
    @Log(title = "修改密码", businessType = BusinessType.SAVE, saveRequestData = false)
    public R<?> updatePwd(@Validated @RequestBody UpdatePwdReq request) {
        var id = UserThreadInfo.get().getUserId();
        loginService.updatePwd(id, request);
        return R.ok();
    }

    @PostMapping("/logout")
    @Log(title = "账号退出", businessType = BusinessType.SELECT, saveRequestData = false)
    public R<?> logout() {
        var loginId = UserThreadInfo.get().getUserId();
        loginService.logout(loginId);
        return R.ok();
    }
}
