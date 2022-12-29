package fml.plus.auth.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface VisitorAccess {

    /**
     * 跳过校验Token是否有效
     */
    boolean skipLogin() default true;

    /**
     * 跳过校验签名
     */
    boolean skipSign() default false;

    /**
     * 特殊接口, 直接跳过整个校验
     */
    boolean special() default false;
}
