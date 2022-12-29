package fml.plus.auth.common.aop.retry;


import fml.plus.auth.common.exception.OptimisticLockException;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Retry {

    /**
     * 最大重试次数
     */
    int max() default 3;

    /**
     * 针对异常
     */
    Class<? extends Throwable> forException() default OptimisticLockException.class;
}
