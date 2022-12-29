package fml.plus.auth.common.redisson.lock.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    /**
     * 默认LockKey
     */
    String value() default "";

    /**
     * 生成锁Key判断条件
     */
    LockConditional[] condition() default {};

    /**
     * 请求锁的等待时间，单位为秒
     */
    int waitTime() default 0;

    /**
     * 使用锁的时间，超时会自动释放，单位为秒
     */
    int keepTime() default 180;

    /**
     * 是否按用户维度生成锁<br>
     */
    boolean lockUser() default false;
}
