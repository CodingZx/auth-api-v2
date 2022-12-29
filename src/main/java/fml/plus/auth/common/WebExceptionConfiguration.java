package fml.plus.auth.common;

import com.google.common.collect.Lists;
import fml.plus.auth.service.ExceptionLogService;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.exception.LockAcquiredFailedException;
import fml.plus.auth.common.exception.LoginException;
import fml.plus.auth.common.exception.UnknownException;
import fml.plus.auth.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class WebExceptionConfiguration implements InitializingBean {
    @Autowired
    private ExceptionLogService logService;
    private static final List<Class<? extends Exception>> IGNORE_EXCEPTION = Lists.newLinkedList();

    static {
        IGNORE_EXCEPTION.add(MissingServletRequestParameterException.class);
        IGNORE_EXCEPTION.add(HttpRequestMethodNotSupportedException.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        // 不打印异常
        if (IGNORE_EXCEPTION.contains(e.getClass())) {
            return R.error(e.getMessage());
        }
        logService.save(e);
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        return R.code(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(LoginException.class)
    public R<?> handleLoginException(LoginException e) {
        return R.login();
    }

    @ResponseBody
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public R<?> handleValidationFailure(Exception ex) {
        FieldError err = null;
        if (ex instanceof BindException) {
            err = ((BindException) ex).getFieldError();
        }
        if (ex instanceof MethodArgumentNotValidException) {
            err = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError();
        }
        if (err != null) {
            return R.other(err.getDefaultMessage());
        }
        throw UnknownException.make(ex);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(LockAcquiredFailedException.class)
    public R<?> handleLockException(LockAcquiredFailedException e) {
        return R.other("请重试");
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("Configuration Spring exception handler..");
    }
}
