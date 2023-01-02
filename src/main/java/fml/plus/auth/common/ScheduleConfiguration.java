package fml.plus.auth.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

@Slf4j
@Configuration
@Import(ScheduleConfiguration.ScheduleProperty.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleConfiguration implements SchedulingConfigurer {
    private ScheduleProperty scheduleProperty;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        log.debug("Configuration Schedule..");
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(scheduleProperty.corePoolSize));
    }

    @Data
    @ConfigurationProperties(prefix = "schedule")
    public static class ScheduleProperty {
        private int corePoolSize = Runtime.getRuntime().availableProcessors() * 5;
    }
}
