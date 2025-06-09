package org.example.restapiscrapper.config;

import org.example.restapiscrapper.common.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean
    public ThreadPoolExecutor pollingExecutor(AppProperties appProperties) {
        return new ThreadPoolExecutor(
                appProperties.getMaxPool(),
                appProperties.getMaxPool(),
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    @Bean
    public ScheduledExecutorService scheduler() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
