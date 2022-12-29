package fml.plus.auth.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MenuReq {
    private UUID id; // 菜单ID, 新增为null
    @NotEmpty(message = "菜单名称不能为空")
    private String title; // 菜单名称
    @NotNull(message = "图标不能为Null")
    private String icon; // 图标, 没有图标则为空字符串
    @NotNull(message = "菜单路径不能为Null")
    private String path; // 菜单标识
    private int sortBy;// 排序值, 小在前
    @NotNull(message = "类型不能为空")
    private String menuType; // 菜单类型 dir(目录) menu(菜单) button(按钮)
    private UUID parentId; // 父级ID, 目录则为空字符串
    @NotNull(message = "菜单权限不能为空")
    private String resourceCode; // 菜单所需权限, 无需权限则为空字符串
}
