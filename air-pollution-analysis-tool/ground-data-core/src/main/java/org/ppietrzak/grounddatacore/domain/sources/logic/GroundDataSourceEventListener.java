package org.ppietrzak.grounddatacore.domain.sources.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.domain.sources.mappers.GroundDataSourceMapper;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class GroundDataSourceEventListener {

    private final GroundDataSourceService groundDataSourceService;
    private final GroundDataSourceMapper groundDataSourceMapper;

    @KafkaListener(
            topics = "${configuration.kafka.topics.sourceTopic.name}",
            groupId = "${configuration.kafka.consumer.groupId}",
            containerFactory = "groundStationsKafkaListenerContainerFactory"
    )
    public void processSourceRegistration(GroundDataSourceDTO groundDataSource) {
        GroundDataSource source = groundDataSourceMapper.toEntity(groundDataSource);
        groundDataSourceService.saveGroundDataSource(source);
        log.info("Registered new datasource (name: {}): {}", groundDataSource.getName(), groundDataSource);
    }
}