package org.ppietrzak.dataprocessor.domain.satellitemeasurements.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary.SatelliteMeasurementIndexationBatchDTO;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.mappers.SatelliteMeasurementDTOMapper;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.model.SatelliteMeasurement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SatelliteMeasurementsEventListener {

    private final SatelliteMeasurementDTOMapper satelliteMeasurementDTOMapper;
    private final SatelliteMeasurementsService satelliteMeasurementsService;
    private final OrderService orderService;

    @KafkaListener(
            topics = "${configuration.kafka.topics.satellite-measurements.name}",
            groupId = "${configuration.kafka.consumer.groupId}",
            containerFactory = "satelliteMeasurementsKafkaListenerContainerFactory"
    )
    public void processSourceRegistration(SatelliteMeasurementIndexationBatchDTO indexationBatchDTO) {
        List<SatelliteMeasurement> groundMeasurements = satelliteMeasurementDTOMapper.toEntities(indexationBatchDTO);
        satelliteMeasurementsService.saveAll(groundMeasurements);
        if (indexationBatchDTO.isLastBatch()) {
            updateOrder(indexationBatchDTO.getOrderId(), true);
        }
    }

    private void updateOrder(String orderId, boolean isSatelliteDateFetched) {
        Order order = orderService.getOrderById(orderId);
        orderService.save(order.toBuilder()
                .isSatelliteDataReady(isSatelliteDateFetched)
                .lastUpdatedDate(OffsetDateTime.now(ZoneId.systemDefault()))
                .build());
    }
}
