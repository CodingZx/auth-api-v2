package fml.plus.auth.entity;

import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "a_menu", remark = "菜单信息")
public class MenuEntity {

    @Column(value = "id", id = true)
    private UUID id; // ID
    @Column(value = "menu_name", remark = "菜单名称")
    private String menuName;
    @Column(value = "icon", remark = "显示图标 svg")
    private String icon;
    @Column(value = "sort_by", remark = "排序值  小在前")
    private Integer sortBy;
    @Column(value = "menu_path", remark = "菜单路径")
    private String menuPath;
    @Column(value = "menu_type", remark = "菜单类型")
    private MenuType menuType;
    @Column(value = "parent_id", remark = "父级ID")
    private UUID parentId;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;
    @Column(value = "permission", remark = "权限")
    private String permission;

    @Getter
    public enum MenuType {

        Dir("dir"), // 目录
        Menu("menu"), // 菜单
        Button("button"), // 按钮
        ;

        private final String code;

        MenuType(String code) {
            this.code = code;
        }

        public static MenuType of(String code) {
            for(var type : values()) {
                if(type.code.equals(code)) return type;
            }
            return null;
        }
    }
}
