package com.github.f.plan.econnoisseur.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ScheduleConfiguration
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月28日 16:51:00
 */
@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod="shutdown")
    public ScheduledExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }
}
