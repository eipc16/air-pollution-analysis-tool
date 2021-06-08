package org.ppietrzak.grounddatacore.domain.sources.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceIndexationStatusDTO;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.sources.repository.mongo.GroundDataSourceMongoRepository;
import org.ppietrzak.grounddatacore.domain.sources.repository.redis.GroundDataSourceCacheRepository;
import org.ppietrzak.grounddatacore.infrastructure.exceptions.GroundDataSourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroundDataSourceService {

    private final GroundDataSourceMongoRepository repository;
    private final GroundDataSourceCacheRepository cacheRepository;

    public Page<GroundDataSource> findAll(Pageable pageable) {
        return cacheRepository.findAll(pageable);
    }

    public List<GroundDataSource> findAll() {
        return StreamSupport.stream(cacheRepository.findAll().spliterator(), false).collect(Collectors.toUnmodifiableList());
    }

    public GroundDataSource getSourceByName(String sourceName) {
        Optional<GroundDataSource> cachedSource = cacheRepository.findGroundDataSourceByName(sourceName);
        if (cachedSource.isPresent()) {
            return cachedSource.get();
        }
        Optional<GroundDataSource> databaseSource = repository.findGroundDataSourceByName(sourceName);
        databaseSource.ifPresent(source -> {
            log.info("Putting ground data source in cache: {}", source.toString());
            saveGroundDataSourceInCache(source);
        });
        return databaseSource.orElseThrow(() -> new GroundDataSourceNotFoundException(sourceName));
    }

    private void saveGroundDataSourceInCache(GroundDataSource groundDataSource) {
        cacheRepository.save(groundDataSource);
    }

    public void saveGroundDataSource(GroundDataSource groundDataSource) {
        GroundDataSource sourceToSave = groundDataSource.toBuilder()
                .lastUpdated(OffsetDateTime.now(ZoneId.systemDefault()))
                .build();
        repository.save(sourceToSave);
        saveGroundDataSourceInCache(sourceToSave);
    }

    private void reindexSource(GroundDataSource groundDataSource) {
        cacheRepository.delete(groundDataSource);
        repository.delete(groundDataSource);
        saveGroundDataSource(groundDataSource);
    }

    public void reindexAllSources() {
        cacheRepository.findAll().forEach(this::reindexSource);
    }

    public void refreshCache() {
        cacheRepository.deleteAll();
        List<GroundDataSource> savedSources = repository.findAll();
        cacheRepository.saveAll(savedSources);
    }

    public void removeAll() {
        cacheRepository.deleteAll();
        repository.deleteAll();
    }
}
