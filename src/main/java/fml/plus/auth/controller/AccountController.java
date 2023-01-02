package fml.plus.auth.controller;

import com.google.common.base.Strings;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.dto.req.ResetPwdReq;
import fml.plus.auth.dto.resp.AdminResp;
import fml.plus.auth.service.AccountService;
import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.R;
import fml.plus.auth.dto.req.AdminReq;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;

    @Authority("auth:account")
    @GetMapping("/list")
    @Log(title = "用户列表", businessType = BusinessType.SELECT)
    public R<Pager<AdminResp>> list(@RequestParam("page") int page,
                                    @RequestParam("size") int size,
                                    @RequestParam(value = "userName", required = false) String userName,
                                    @RequestParam(value = "realName", required = false) String realName) {
        userName = Strings.nullToEmpty(userName).trim();
        realName = Strings.nullToEmpty(realName).trim();
        var pager = accountService.list(Page.of(page, size), userName, realName);
        return R.ok(pager);
    }

    @Authority({"auth:account:edit", "auth:account:add"})
    @PostMapping("/save")
    @Log(title = "保存用户", businessType = BusinessType.SAVE)
    public R<?> save(@RequestBody @Validated AdminReq admin) {
        admin.setPassword(admin.getPassword() == null ? "" : admin.getPassword().trim());
        admin.setPassword(admin.getPassword().trim());
        if(admin.getId() == null && Strings.isNullOrEmpty(admin.getPassword())) {
            return R.other("密码不能为空");
        }
        if(!Strings.isNullOrEmpty(admin.getPassword()) && (admin.getPassword().length() < 3 || admin.getPassword().length() > 20)) {
            return R.other("密码长度必须在3-20之间");
        }
        accountService.save(admin);
        return R.ok();
    }

    @Authority("auth:account:reset")
    @PutMapping("/reset/pwd")
    public R<?> resetPwd(@RequestBody @Validated ResetPwdReq req) {
        if (req.getAdminId().equals(GlobalConstants.DEFAULT_ID)) {
            return R.other("无法重置超级管理员密码!");
        }
        if(req.getPassword().length() < 3 || req.getPassword().length() > 20) {
            return R.other("密码长度必须在3-20之间");
        }
        accountService.resetPwd(req);
        return R.ok();
    }

    @Authority("auth:account:status")
    @PutMapping("/update/{id}/{status}")
    public R<?> updateStatus(@PathVariable UUID id, @PathVariable boolean status) {
        if(id.equals(GlobalConstants.DEFAULT_ID)) {
            return R.other("无法修改超级管理员状态");
        }
        accountService.updateStatus(id, status);
        return R.ok();
    }

    @Authority("auth:account:delete")
    @DeleteMapping("/{ids}")
    @Log(title = "删除用户", businessType = BusinessType.DELETE)
    public R<?> remove(@PathVariable("ids") UUID[] ids) {
        if (Arrays.asList(ids).contains(GlobalConstants.DEFAULT_ID)) {
            return R.other("无法删除超级管理员!");
        }
        accountService.remove(Arrays.asList(ids));
        return R.ok();
    }
}
