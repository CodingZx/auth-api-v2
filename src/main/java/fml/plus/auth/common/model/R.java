package fml.plus.auth.common.model;

import fml.plus.auth.common.enums.ResultCode;
import lombok.Getter;

public final class R<T> {
    @Getter
    private final int code;
    @Getter
    private final String message;
    @Getter
    private final T data;

    private R(int code, String message) {
        this(code, message, null);
    }

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.OK.getCode(), "success", data);
    }

    public static <T> R<T> other(String message) {
        return new R<>(ResultCode.OTHER.getCode(), message);
    }

    public static <T> R<T> error(String message) {
        return new R<>(ResultCode.ERROR.getCode(), message);
    }

    public static <T> R<T> code(int code, String message) {
        return new R<>(code, message);
    }

    public static <T> R<T> login() {
        return new R<>(ResultCode.LOGIN.getCode(), "登录过期");
    }

    public static <T> R<T> auth() {
        return new R<>(ResultCode.AUTH.getCode(), "无访问权限");
    }
}
