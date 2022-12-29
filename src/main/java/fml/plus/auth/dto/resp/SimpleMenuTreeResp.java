package fml.plus.auth.dto.resp;

import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class SimpleMenuTreeResp {
    private UUID id; // 菜单ID
    private String title; // 菜单名称
    private UUID parentId; // 父级ID 目录为空字符串
    private String menuType; // 菜单类型

    private List<SimpleMenuTreeResp> children; //子项菜单

    public static SimpleMenuTreeResp convert(MenuTreeResp tree) {
        var model = new SimpleMenuTreeResp();
        model.id = tree.getId();
        model.title = tree.getTitle();
        model.parentId = tree.getParentId();
        model.menuType = tree.getMenuType();
        model.children = tree.getChildren() == null ? List.of() : tree.getChildren().stream().map(SimpleMenuTreeResp::convert).collect(Collectors.toList());
        return model;
    }
}
