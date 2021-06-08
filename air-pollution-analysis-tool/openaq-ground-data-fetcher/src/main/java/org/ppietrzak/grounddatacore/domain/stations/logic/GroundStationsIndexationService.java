package org.ppietrzak.grounddatacore.domain.stations.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceIndexationStatusDTO;
import org.ppietrzak.grounddatacore.api.stations.GroundDataStationDTO;
import org.ppietrzak.grounddatacore.api.stations.GroundStationIndexationBatchDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQGroundStationDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQGroundStationPaginatedResultDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQParameterDTO;
import org.ppietrzak.grounddatacore.client.openaq.entities.StationsMetaInfo;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.grounddatacore.config.source.SourceConfigurationProperties;
import org.ppietrzak.grounddatacore.infrastructure.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@ApplicationScope
@RequiredArgsConstructor
@Slf4j
public class GroundStationsIndexationService {

    private static final Integer BATCH_SIZE = 500;

    private final AtomicLong documentCounter = new AtomicLong(0);

    private GroundDataSourceIndexationStatusDTO indexationStatus = initialIndexationStatus();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    private final KafkaTemplate<String, GroundStationIndexationBatchDTO> stationIndexationTemplate;
    private final GroundDataStationsService groundDataStationsService;
    private final SourceConfigurationProperties sourceConfigurationProperties;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    private static GroundDataSourceIndexationStatusDTO initialIndexationStatus() {
        return GroundDataSourceIndexationStatusDTO.builder()
                .indexedObjects(0)
                .totalObjects(0)
                .status(GroundDataSourceIndexationStatusDTO.Status.NOT_STARTED)
                .startTime(null)
                .endTime(null)
                .build();
    }

    public GroundDataSourceIndexationStatusDTO indexAllStations() {
        if (GroundDataSourceIndexationStatusDTO.Status.PENDING.equals(indexationStatus.getStatus())) {
            return indexationStatus;
        }
        StationsMetaInfo stationsMetaInfo = getStationsMetaInfo(BATCH_SIZE);
        indexLocations(stationsMetaInfo);
        return indexationStatus;
    }

    private StationsMetaInfo getStationsMetaInfo(int count) {
        return groundDataStationsService.getStationsMetaInfo(count);
    }

    private CompletableFuture<Void> indexLocations(StationsMetaInfo metaInfo) {
        setPendingIndexationStatus(metaInfo);
        List<CompletableFuture<Void>> allFutures = IntStream.range(0, metaInfo.getTotalPages())
                .map(i -> i + 1)
                .mapToObj(page -> indexLocationsPageAsync(page, metaInfo.getTotalPages()))
                .collect(Collectors.toList());
        return CompletableFuture.allOf(allFutures.toArray(CompletableFuture[]::new))
                .thenAccept(v -> setSuccess())
                .exceptionally(this::setError);
    }

    private void setPendingIndexationStatus(StationsMetaInfo metaInfo) {
        this.indexationStatus = indexationStatus.toBuilder()
                .startTime(LocalDateTime.now())
                .endTime(null)
                .indexedObjects(0L)
                .totalObjects(metaInfo.getTotalStations())
                .errorCause(null)
                .status(GroundDataSourceIndexationStatusDTO.Status.PENDING)
                .build();
    }

    @Async("indexationThreadPoolExecutor")
    protected CompletableFuture<Void> indexLocationsPageAsync(int page, int totalPages) {
        return CompletableFuture.runAsync(() -> indexLocationsPage(page, totalPages));
    }

    private void indexLocationsPage(int page, int totalPages) {
        OpenAQGroundStationPaginatedResultDTO groundStationsPage =
                groundDataStationsService.getStations(page, BATCH_SIZE);
        ListenableFuture<SendResult<String, GroundStationIndexationBatchDTO>> future = stationIndexationTemplate.send(
                kafkaConfigurationProperties.getTopicName(KafkaTopics.STATIONS_TOPIC),
                "OpenAQPage_" + page,
                buildIndexationBatch(groundStationsPage, page, totalPages)
        );
        updateIndexedObjectCounter(groundStationsPage.getContent().size());
    }

    private void updateIndexedObjectCounter(int newIndexedObjets) {
        documentCounter.getAndAdd(newIndexedObjets);
    }

    private GroundStationIndexationBatchDTO buildIndexationBatch(OpenAQGroundStationPaginatedResultDTO groundStationsPage, int page, int totalPages) {
        return GroundStationIndexationBatchDTO.builder()
                .source(sourceConfigurationProperties.getName())
                .batchSize(groundStationsPage.getContent().size())
                .currentBatch(page)
                .totalBatches(totalPages)
                .stations(groundStationsPage.getContent().stream()
                        .filter(station -> station.getCoordinates() != null)
                        .map(this::buildGroundDataStation)
                        .collect(Collectors.toUnmodifiableList()))
                .build();
    }

    private GroundDataStationDTO buildGroundDataStation(OpenAQGroundStationDTO openAQGroundStationDTO) {
        return GroundDataStationDTO.builder()
                .id(String.valueOf(openAQGroundStationDTO.getId()))
                .name(openAQGroundStationDTO.getName())
                .latitude(openAQGroundStationDTO.getCoordinates().getLatitude())
                .longitude(openAQGroundStationDTO.getCoordinates().getLongitude())
                .parameters(openAQGroundStationDTO.getParameters().stream()
                        .map(OpenAQParameterDTO::getParameter)
                        .collect(Collectors.toList()))
                .build();
    }

    private @Nullable
    Void setSuccess() {
        this.indexationStatus = indexationStatus.toBuilder()
                .status(GroundDataSourceIndexationStatusDTO.Status.FINISHED)
                .endTime(LocalDateTime.now())
                .build();
        return null;
    }

    private @Nullable
    Void setError(Throwable throwable) {
        log.error("Exception encountered while indexation stations", throwable);
        this.indexationStatus = indexationStatus.toBuilder()
                .errorCause(throwable.getCause().getMessage())
                .status(GroundDataSourceIndexationStatusDTO.Status.FAILED)
                .endTime(LocalDateTime.now())
                .build();
        return null;
    }

    public GroundDataSourceIndexationStatusDTO getIndexationStatus() {
        return indexationStatus.toBuilder()
                .indexedObjects(documentCounter.get())
                .build();
    }
}
