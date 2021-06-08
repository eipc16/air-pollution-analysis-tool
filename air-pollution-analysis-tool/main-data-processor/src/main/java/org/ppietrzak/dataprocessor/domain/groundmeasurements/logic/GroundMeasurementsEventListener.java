package org.ppietrzak.dataprocessor.domain.groundmeasurements.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.mappers.GroundMeasurementDTOMapper;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.model.GroundMeasurement;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsIndexationBatchDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class GroundMeasurementsEventListener {

    private final GroundMeasurementDTOMapper groundMeasurementDTOMapper;
    private final GroundMeasurementsService groundMeasurementsService;
    private final OrderService orderService;

    @KafkaListener(
            topics = "${configuration.kafka.topics.ground-measurements.name}",
            groupId = "${configuration.kafka.consumer.groupId}",
            containerFactory = "groundMeasurementsKafkaListenerContainerFactory"
    )
    public void processSourceRegistration(MeasurementsIndexationBatchDTO measurementsIndexationBatchDTO) {
        List<GroundMeasurement> groundMeasurements = groundMeasurementDTOMapper.toEntities(measurementsIndexationBatchDTO);
        groundMeasurementsService.saveAll(groundMeasurements);
        if (measurementsIndexationBatchDTO.isLastBatch()) {
            updateOrder(measurementsIndexationBatchDTO.getOrderId(), true);
        }
    }

    private void updateOrder(String orderId, boolean isGroundDataFetchFinished) {
        Order order = orderService.getOrderById(orderId);
        orderService.save(order.toBuilder()
                .isGroundDataReady(isGroundDataFetchFinished)
                .lastUpdatedDate(OffsetDateTime.now(ZoneId.systemDefault()))
                .build());
    }
}
