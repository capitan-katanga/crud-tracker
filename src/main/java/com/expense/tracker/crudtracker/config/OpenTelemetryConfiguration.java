package com.expense.tracker.crudtracker.config;

import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenTelemetryConfiguration {

    // Add OpenTelemetry semantic conventions.
    // See https://github.com/spring-projects/spring-boot/pull/47935
    @Bean
    @ConditionalOnMissingBean
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

}
