package org.ppietrzak.grounddatacore.domain.sources.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.client.sources.SourceFetcherClient;
import org.ppietrzak.grounddatacore.config.indexation.cache.DataSourcesCacheConfigurationProperties;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.sources.repository.mongo.GroundDataSourceMongoRepository;
import org.ppietrzak.grounddatacore.domain.sources.repository.redis.GroundDataSourceCacheRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Slf4j
@ApplicationScope
@RequiredArgsConstructor
public class GroundDataSourceCacheInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    private final DataSourcesCacheConfigurationProperties cacheConfigurationProperties;
    private final GroundDataSourceMongoRepository groundDataSourceRepository;
    private final GroundDataSourceCacheRepository groundDataSourceCacheRepository;
    private final SourceFetcherClient sourceFetcherClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        scheduledExecutorService.scheduleAtFixedRate(
                this::refreshAllSources,
                cacheConfigurationProperties.getInitialDelay(),
                cacheConfigurationProperties.getInterval(),
                cacheConfigurationProperties.getUnit());
    }

    private void refreshAllSources() {
        log.info("Refreshing ground data sources cache...");
        long cachedSourcesSize = StreamSupport.stream(groundDataSourceCacheRepository.findAll().spliterator(), false).count();
        groundDataSourceCacheRepository.deleteAll();
        Map<Boolean, List<GroundDataSource>> newCachedSourcePartitionedByHealthState = groundDataSourceRepository.findAll().stream()
                .collect(Collectors.partitioningBy(sourceFetcherClient::isHealthy));
        List<GroundDataSource> newCachedDataSources = newCachedSourcePartitionedByHealthState.getOrDefault(true, Collections.emptyList());
        List<GroundDataSource> unhealthySources = newCachedSourcePartitionedByHealthState.getOrDefault(false, Collections.emptyList());
        if(!unhealthySources.isEmpty()) {
            groundDataSourceRepository.deleteAll(unhealthySources);
            log.info("Removed {} unhealthy sources. Names: {}", unhealthySources.size(), unhealthySources.stream()
                    .map(GroundDataSource::getName)
                    .collect(Collectors.toList()));
        }
        groundDataSourceCacheRepository.saveAll(newCachedDataSources);
        log.info("Finished refreshing ground data sources cache. New sources count: {}. Old sources count: {}", newCachedDataSources.size(), cachedSourcesSize);
    }
}
