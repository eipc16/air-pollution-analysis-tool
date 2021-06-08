package org.ppietrzak.grounddatacore.domain.measurements.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsIndexationBatchDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsPageDTO;
import org.ppietrzak.grounddatacore.client.sources.SourceFetcherClient;
import org.ppietrzak.grounddatacore.domain.measurements.mappers.MeasurementDTOMapper;
import org.ppietrzak.grounddatacore.domain.measurements.model.Measurement;
import org.ppietrzak.grounddatacore.domain.sources.logic.GroundDataSourceService;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.stations.logic.CoordinatesBox;
import org.ppietrzak.grounddatacore.domain.stations.logic.GroundStationsService;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
@Slf4j
public class MeasurementsService {

    private static final int FETCH_DOWNLOAD_SIZE = 10000;
    private static final int BATCH_SIZE = 1000;

    private final SourceFetcherClient sourceFetcherClient;
    private final GroundDataSourceService groundDataSourceService;
    private final GroundStationsService groundStationsService;
    private final MeasurementDTOMapper measurementDTOMapper;
    private final KafkaTemplate<String, MeasurementsIndexationBatchDTO> measurementsIndexationTemplate;

    public Page<Measurement> fetchByCoords(String sourceName,
                                           String parameterName,
                                           CoordinatesBox coordinatesBox,
                                           OffsetDateTime from,
                                           OffsetDateTime to,
                                           Pageable pageable) {
        GroundDataSource source = groundDataSourceService.getSourceByName(sourceName);
        List<Long> groundStationIds = getGroundStationIds(groundStationsService.findByCoordsAndParameter(source.getName(), parameterName, coordinatesBox));
        MeasurementsPageDTO measurementsPageDTO = sourceFetcherClient.getMeasurements(source, groundStationIds, parameterName, from, to, pageable.getPageNumber(), pageable.getPageSize());
        List<Measurement> measurements = measurementsPageDTO.getMeasurements().stream()
                .map(measurementDTOMapper::toEntity)
                .collect(Collectors.toUnmodifiableList());
        return PageableExecutionUtils.getPage(
                measurements,
                pageable,
                measurementsPageDTO::getTotalCount
        );
    }

    private List<Long> getGroundStationIds(Collection<GroundStation> groundStations) {
        return groundStations.stream()
                .map(GroundStation::getId)
                .map(Long::valueOf)
                .collect(Collectors.toUnmodifiableList());
    }

    @Async("indexationThreadPoolExecutor")
    public CompletableFuture<Void> indexMeasurementsAsynchronously(String sourceName, String parameterName,
                                                                   String targetTopic, String orderId,
                                                                   List<GroundStation> stations,
                                                                   OffsetDateTime from, OffsetDateTime to) {
        return CompletableFuture.runAsync(() -> indexMeasurements(
                sourceName, parameterName, targetTopic, orderId, stations, from, to));
    }

    private void indexMeasurements(String sourceName, String parameterName,
                                   String targetTopic, String orderId,
                                   List<GroundStation> stations,
                                   OffsetDateTime from, OffsetDateTime to) {
        GroundDataSource source = groundDataSourceService.getSourceByName(sourceName);
        List<Long> groundStationIds = getGroundStationIds(stations);

        MeasurementsPageDTO measurementsPageDTO = sourceFetcherClient.getMeasurements(source, groundStationIds, parameterName, from, to, 0, FETCH_DOWNLOAD_SIZE);
        sendEvent(targetTopic, orderId, 1, measurementsPageDTO);

        while (measurementsPageDTO.hasNextPage()) {
            int nextPage = (int) measurementsPageDTO.getCurrentPage() + 1;
            measurementsPageDTO = sourceFetcherClient.getMeasurements(
                    source, groundStationIds, parameterName, from, to, nextPage, FETCH_DOWNLOAD_SIZE);
            sendEvent(targetTopic, orderId, nextPage + 1, measurementsPageDTO);
        }
    }

    private void sendEvent(String targetTopic, String orderId, int fetchPart, MeasurementsPageDTO measurementsPageDTO) {
        List<MeasurementDTO> measurements = measurementsPageDTO.getMeasurements();
        List<List<MeasurementDTO>> batchParts = batch(measurements, BATCH_SIZE);
        int totalBatchParts = batchParts.size();
        int batchPart = 1;

        if(batchParts.isEmpty()) {

            measurementsIndexationTemplate.send(
                    targetTopic,
                    "GroundMeasurements_" + orderId + "_" + fetchPart,
                    buildIndexationBatch(
                            orderId,
                            fetchPart, measurementsPageDTO.getTotalPages(),
                            1, 1,
                            Collections.emptyList()
                    )
            );
            log.info("Sending event... Batch: {}/{}, Batch Part: {}/{}", fetchPart, fetchPart, 1, 1);
            return;
        }

        for (List<MeasurementDTO> batch: batchParts) {
            measurementsIndexationTemplate.send(
                    targetTopic,
                    "GroundMeasurements_" + orderId + "_" + fetchPart + "_" + batchPart,
                    buildIndexationBatch(
                            orderId,
                            fetchPart, measurementsPageDTO.getTotalPages(),
                            batchPart, totalBatchParts,
                            batch)
            );
            log.info("Sending event... Batch: {}/{}, Batch Part: {}/{}", fetchPart, measurementsPageDTO.getTotalPages(), batchPart, totalBatchParts);
            batchPart++;
        }
    }

    private List<List<MeasurementDTO>> batch(List<MeasurementDTO> measurements, int batch_size) {
        return IntStream.range(0, measurements.size())
                .boxed()
                .collect(Collectors.groupingBy(index -> index / batch_size))
                .values()
                .stream()
                .map(indices -> indices.stream()
                        .map(measurements::get)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private MeasurementsIndexationBatchDTO buildIndexationBatch(String orderId,
                                                                int fetchCount, long totalPages,
                                                                int batchPart, int totalBatchParts,
                                                                List<MeasurementDTO> measurements) {
        return MeasurementsIndexationBatchDTO.builder()
                .currentBatch(fetchCount)
                .totalBatches(totalPages)
                .orderId(orderId)
                .measurements(measurements)
                .batchPart(batchPart)
                .totalBatchParts(totalBatchParts)
                .build();
    }
}
