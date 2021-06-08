package org.ppietrzak.dataprocessor.domain.orders.logic;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.grounddatacore.GroundDataCoreClient;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteDataCoreClient;
import org.ppietrzak.dataprocessor.domain.calculationrequests.services.CalculationRequestService;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.repository.mongo.GroundMeasurementsMongoRepository;
import org.ppietrzak.dataprocessor.domain.orders.boundary.OrderRequestDTO;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.orders.repository.mongo.OrderMongoRepository;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.repository.mongo.SatelliteMeasurementsMongoRepository;
import org.ppietrzak.dataprocessor.infrastructure.kafka.KafkaTopics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMongoRepository orderMongoRepository;
    private final GroundMeasurementsMongoRepository groundMeasurementsMongoRepository;
    private final SatelliteMeasurementsMongoRepository satelliteMeasurementsMongoRepository;
    private final CalculationRequestService calculationRequestService;


    private final GroundDataCoreClient groundDataCoreClient;
    private final SatelliteDataCoreClient satelliteDataCoreClient;

    public Order placeOrder(OrderRequestDTO orderRequestDTO) {
        Order order = Order.builder()
                .name(orderRequestDTO.getName())
                .createDate(OffsetDateTime.now(ZoneId.systemDefault()))
                .userId("0")
                .initialModel(orderRequestDTO.getModel())
                .distance(orderRequestDTO.getDistance())
                .startDate(orderRequestDTO.getDateFromAsOffsetDateTime())
                .endDate(orderRequestDTO.getDateToAsOffsetDateTime())
                .bottomLatitude(orderRequestDTO.getBottomLatitude())
                .bottomLongitude(orderRequestDTO.getBottomLongitude())
                .upperLatitude(orderRequestDTO.getUpperLatitude())
                .upperLongitude(orderRequestDTO.getUpperLongitude())
                .groundSource(orderRequestDTO.getGroundSource())
                .satelliteSource(orderRequestDTO.getSatelliteSource())
                .groundParameter(orderRequestDTO.getGroundParameter())
                .satelliteParameter(orderRequestDTO.getSatelliteParameter())
                .build();
        Order savedOrder = save(order);

        // Start fetching ground data
        groundDataCoreClient.startFetchingGroundData(KafkaTopics.GROUND_MEASUREMENTS_TOPIC, savedOrder);

        // Start fetching satellite data
        satelliteDataCoreClient.startFetchingGroundData(KafkaTopics.SATTELITE_MEASUREMENTS_TOPIC, savedOrder);

        return savedOrder;
    }

    public Order getOrderById(String orderId) {
        return orderMongoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Order with id: {0} not found", orderId)));
    }

    public Order save(Order order) {
        return orderMongoRepository.save(order);
    }

    public List<Order> getOrdersWithCompleteAndNotCalculatedDataAndModelSpecified() {
        return orderMongoRepository.getOrdersWithCompleteAndNotCalculatedDataAndModelSpecified();
    }

    public Page<Order> findAll(int page, int size) {
        return orderMongoRepository.findAll(PageRequest.of(page, size));
    }

    public boolean removeOrder(String orderId) {
        Order order = getOrderById(orderId);
        groundMeasurementsMongoRepository.deleteAllByOrderId(order.getId());
        satelliteMeasurementsMongoRepository.deleteAllByOrderId(order.getId());
        return calculationRequestService.removeByOrderId(order.getId());
    }
}
