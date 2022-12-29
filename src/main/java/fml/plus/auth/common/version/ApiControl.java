package fml.plus.auth.common.version;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiControl {
    /**
     * 控制版本号
     */
    String version();
}
