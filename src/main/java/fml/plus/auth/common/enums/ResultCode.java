package fml.plus.auth.common.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    OK(200),            // 成功 返回Code
    OTHER(400),         // 业务处理失败 返回Code
    ERROR(500),         // 系统异常 返回Code
    AUTH(888),          // 权限认证不通过 返回Code
    LOGIN(999),         // 需要登录 返回Code
    ;
    private final int code;

    ResultCode(int code) {
        this.code = code;
    }
}
