package fml.plus.auth.controller;

import com.google.common.base.Strings;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.model.Selector;
import fml.plus.auth.dto.resp.RoleResp;
import fml.plus.auth.service.RoleService;
import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.R;
import fml.plus.auth.dto.req.RoleReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Authority({"auth:admin:add", "auth:admin:edit"})
    @GetMapping("/all")
    public R<List<Selector>> allRole() {
        return R.ok(roleService.all());
    }

    @Authority("auth:role")
    @GetMapping("/list")
    @Log(title = "角色列表", businessType = BusinessType.SELECT)
    public R<Pager<RoleResp>> list(@RequestParam("page") int page,
                                   @RequestParam("size") int size,
                                   @RequestParam(value = "roleName",required = false) String roleName) {
        var pager = roleService.list(Page.of(page, size), Strings.nullToEmpty(roleName));
        return R.ok(pager);
    }


    @Authority({"auth:role:add", "auth:role:edit"})
    @GetMapping("/find/{roleId}/menus")
    public R<List<UUID>> list(@PathVariable UUID roleId) {
        return R.ok(roleService.findMenuIdsById(roleId));
    }

    @Authority({"auth:role:add", "auth:role:edit"})
    @PostMapping("/save")
    @Log(title = "保存角色", businessType = BusinessType.SAVE)
    public R<?> save(@RequestBody @Validated RoleReq role) {
        roleService.save(role);
        return R.ok();
    }

    @Authority("auth:role:delete")
    @DeleteMapping("/{ids}")
    @Log(title = "删除角色", businessType = BusinessType.DELETE)
    public R<?> remove(@PathVariable("ids") UUID[] ids) {
        if(Arrays.asList(ids).contains(GlobalConstants.DEFAULT_ID)) {
            return R.other("无法删除超级管理员!");
        }
        roleService.remove(Arrays.asList(ids));
        return R.ok();
    }
}
