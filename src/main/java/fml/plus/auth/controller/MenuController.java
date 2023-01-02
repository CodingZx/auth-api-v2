package fml.plus.auth.controller;

import fml.plus.auth.dto.resp.MenuTreeResp;
import fml.plus.auth.service.MenuService;
import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.model.R;
import fml.plus.auth.dto.req.MenuReq;
import fml.plus.auth.dto.resp.SimpleMenuTreeResp;
import fml.plus.auth.entity.MenuEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static fml.plus.auth.common.constants.GlobalConstants.MENU_ID;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/menu")
public class MenuController {
    private MenuService menuService;

    @Authority("auth:menu")
    @GetMapping("/list")
    @Log(title = "查询菜单", businessType = BusinessType.SELECT)
    public R<List<MenuTreeResp>> list(){
        return R.ok(menuService.list());
    }

    @Authority({"auth:role:add", "auth:role:edit"})
    @GetMapping("/all")
    public R<List<SimpleMenuTreeResp>> all(){
        return R.ok(menuService.list().stream().map(menu -> {
            var respMenu = SimpleMenuTreeResp.convert(menu);
            respMenu.setChildren(respMenu.getChildren().stream().filter(r -> {
                if(r.getId().equals(MENU_ID)) { // 过滤菜单显示
                    return UserThreadInfo.get().isSuperAdmin();
                }
                return true;
            }).collect(Collectors.toList()));
            return respMenu;
        }).collect(Collectors.toList()));
    }

    @Authority({"auth:menu:add", "auth:menu:edit"})
    @PostMapping("/save")
    @Log(title = "保存菜单", businessType = BusinessType.SAVE)
    public R<?> save(@RequestBody @Validated MenuReq menu){
        var menuType = MenuEntity.MenuType.of(menu.getMenuType().toLowerCase());
        if(menuType == null) {
            return R.other("菜单类型不正确");
        }
        menuService.save(menu);
        return R.ok();
    }

    @Authority("auth:menu:delete")
    @DeleteMapping("/{ids}")
    @Log(title = "删除菜单", businessType = BusinessType.DELETE)
    public R<?> remove(@PathVariable("ids") UUID[] ids){
        menuService.remove(Arrays.asList(ids));
        return R.ok();
    }
}
