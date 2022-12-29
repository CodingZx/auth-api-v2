package fml.plus.auth.common.mybatis.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * 表名称
     */
    String name();

    /**
     * 备注，仅用于在注解上展示，不用于任何其他处理
     */
    String remark() default "";
}
