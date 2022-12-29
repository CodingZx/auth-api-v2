package fml.plus.auth.common.aop.log;

import lombok.Getter;

/**
 * 操作类型
 */
@Getter
public enum BusinessType {
    OTHER(0, "其他"),

    /**
     * 保存
     */
    SAVE(1, "保存"),

    /**
     * 查询
     */
    SELECT(2, "查询"),

    /**
     * 删除
     */
    DELETE(3, "删除"),

    ;
    private final int type;
    private final String name;

    BusinessType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static BusinessType of(int type) {
        for(var val : values()) {
            if(val.type == type) return val;
        }
        return OTHER;
    }
}
