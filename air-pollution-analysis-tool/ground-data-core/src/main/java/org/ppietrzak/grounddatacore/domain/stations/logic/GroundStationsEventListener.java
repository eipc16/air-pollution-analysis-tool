package org.ppietrzak.grounddatacore.domain.stations.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.stations.GroundStationIndexationBatchDTO;
import org.ppietrzak.grounddatacore.domain.stations.mappers.GroundStationDTOMapper;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class GroundStationsEventListener {

    private final GroundStationsService groundStationsService;
    private final GroundStationDTOMapper groundStationDTOMapper;

    @KafkaListener(
            topics = "${configuration.kafka.topics.stationsTopic.name}",
            groupId = "${configuration.kafka.consumer.groupId}",
            containerFactory = "groundStationsKafkaListenerContainerFactory"
    )
    public void processSourceRegistration(GroundStationIndexationBatchDTO groundStationIndexationBatchDTO) {
        String source = groundStationIndexationBatchDTO.getSource();
        List<GroundStation> stations = groundStationIndexationBatchDTO.getStations().stream()
                .map(entity -> groundStationDTOMapper.mapToEntity(source, entity))
                .collect(Collectors.toUnmodifiableList());
        groundStationsService.saveStations(stations);
    }
}
