package de.breuninger.coding.challenge.homefeed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfiguration {

    private final ExecutorConfigurationProperties properties;

    public ExecutorConfiguration(ExecutorConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "homefeedModuleExecutor", destroyMethod = "shutdown")
    public ExecutorService homefeedModuleExecutor() {

        // Thread pool uses a cached one where the size is reduced ot min size, when a thread is no longer needed (lifetime goes over keep alive)
        // Backpressure is handled through the policy where calling thread will start executing when the pool is full and queue is also full
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueCapacity()),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("homefeed-module-" + thread.threadId());
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}


