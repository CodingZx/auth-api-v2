package fml.plus.auth.common;

import com.google.gson.Gson;
import fml.plus.auth.common.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Slf4j
@Configurable
public class GsonConfiguration {

    @Bean
    public Gson gson() {
        log.debug("Configuration Gson..");
        return GsonUtils.gson();
    }
}
