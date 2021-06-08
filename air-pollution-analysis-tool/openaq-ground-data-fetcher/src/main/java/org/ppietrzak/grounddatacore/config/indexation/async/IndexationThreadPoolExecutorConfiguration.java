package org.ppietrzak.grounddatacore.config.indexation.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class IndexationThreadPoolExecutorConfiguration {

    @Bean(name = "indexationThreadPoolExecutor")
    public Executor indexationThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("StationIndexation-");
        executor.initialize();
        return executor;
    }
}
