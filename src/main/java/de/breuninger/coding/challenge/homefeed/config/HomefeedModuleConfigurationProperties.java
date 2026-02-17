package de.breuninger.coding.challenge.homefeed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "homefeed.modules")
public class HomefeedModuleConfigurationProperties {
    private Map<String, Integer> priorities;
    private long executionTimeoutMs = 2000; // default 2 seconds

    public void setPriorities(Map<String, Integer> priorities) {
        this.priorities = priorities;
    }

    public Map<String, Integer> getPriorities() {
        return priorities;
    }

    public long getExecutionTimeoutMs() {
        return executionTimeoutMs;
    }

    public void setExecutionTimeoutMs(long executionTimeoutMs) {
        this.executionTimeoutMs = executionTimeoutMs;
    }
}
