package fml.plus.auth.dto.resp;

import fml.plus.auth.entity.MenuEntity;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MenuTreeResp {
    private UUID id; // 菜单ID
    private String title; // 菜单名称
    private String resourceCode; // 权限Code
    private String path; // 菜单Code
    private String icon; // 菜单图标
    private int sortBy; // 排序值, 小在前

    private String menuType; // 菜单类型 dir(目录) menu(菜单) button(按钮)
    private UUID parentId; // 父级ID, 目录则为空字符串

    private List<MenuTreeResp> children; // 子项菜单

    public MenuTreeResp(MenuEntity menu) {
        this.id = menu.getId();
        this.title = menu.getMenuName();
        this.resourceCode = menu.getPermission();
        this.icon = menu.getIcon();
        this.sortBy = menu.getSortBy();
        this.menuType = menu.getMenuType().getCode();
        this.parentId = menu.getParentId();
        this.path = menu.getMenuPath();
    }
}
