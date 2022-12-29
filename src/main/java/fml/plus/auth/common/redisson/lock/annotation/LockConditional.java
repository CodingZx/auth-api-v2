package fml.plus.auth.common.redisson.lock.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockConditional {
    /**
     * 判断条件
     */
    String conditionCase();

    /**
     * 判断条件成立时, 使用此Key
     */
    String conditionKey();

}
