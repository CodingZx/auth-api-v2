package fml.plus.auth.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean
    public Executor taskExecutor(AsyncProperty property) {
        log.debug("Configuration Async..");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(property.corePoolSize);
        executor.setMaxPoolSize(property.maxPoolSize);
        executor.setQueueCapacity(property.queueCapacity);
        executor.initialize();
        return executor;
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "async")
    public static class AsyncProperty {
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        private int queueCapacity = 1000;
    }
}
