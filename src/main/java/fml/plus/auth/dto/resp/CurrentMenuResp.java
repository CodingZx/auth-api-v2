package fml.plus.auth.dto.resp;

import fml.plus.auth.entity.MenuEntity;
import lombok.Data;

import java.util.List;

@Data
public class CurrentMenuResp {
    private String title; // 菜单显示名称
    private String icon; // 菜单图标
    private String path; // 菜单code
    private List<CurrentMenuResp> children; // 子项菜单

    public CurrentMenuResp(MenuEntity menu) {
        this.title = menu.getMenuName();
        this.icon = menu.getIcon();
        this.path = menu.getMenuPath();
    }
}
