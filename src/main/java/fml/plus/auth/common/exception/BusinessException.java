package fml.plus.auth.common.exception;

import fml.plus.auth.common.enums.ResultCode;
import lombok.Getter;

public class BusinessException extends RuntimeException {

    @Getter
    private final String msg;
    @Getter
    private final int code;

    public BusinessException(String msg) {
        super(msg);
        this.code = ResultCode.OTHER.getCode();
        this.msg = msg;
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
