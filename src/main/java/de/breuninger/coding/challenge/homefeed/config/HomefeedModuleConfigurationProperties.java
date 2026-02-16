package de.breuninger.coding.challenge.homefeed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "homefeed.modules")
public class HomefeedModuleConfigurationProperties {
    private Map<String, Integer> priorities;

    public void setPriorities(Map<String, Integer> priorities) {
        this.priorities = priorities;
    }

    public Map<String, Integer> getPriorities() {
        return priorities;
    }
}
