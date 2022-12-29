package fml.plus.auth.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Slf4j
@Configuration
public class SignConfiguration {

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "sign")
    public static class SignProperty implements InitializingBean {
        private boolean enable; // 是否启用
        private String key; // 签名秘钥Key

        @Override
        public void afterPropertiesSet() {
            if(enable) {
                Assert.hasText(key, "配置项sign.key不能为空, 请检查配置");
            }
        }
    }
}
