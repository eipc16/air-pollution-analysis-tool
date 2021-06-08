package org.ppietrzak.dataprocessor.domain.calculationrequests.services;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.calculationservice.CalculationServiceCoreClient;
import org.ppietrzak.dataprocessor.domain.calculationrequests.boundary.CalculationRequestBodyDTO;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.repository.mongo.CalculationRequestMongoRepository;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.orders.repository.mongo.OrderMongoRepository;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.repository.mongo.SatellitePredictionMongoRepository;
import org.ppietrzak.dataprocessor.domain.testdata.repository.mongo.TestDataMongoRepository;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationRequestService {

    private final CalculationRequestMongoRepository calculationRequestRepository;
    private final CalculationServiceCoreClient calculationServiceCoreClient;
    private final TestDataMongoRepository testDataMongoRepository;
    private final SatellitePredictionMongoRepository satellitePredictionMongoRepository;
    private final OrderMongoRepository orderMongoRepository;

    public CalculationRequest getById(String calculationRequestId) {
        return calculationRequestRepository.findById(calculationRequestId)
                .orElseThrow(() -> new IllegalArgumentException("No calculation request with id: " + calculationRequestId));
    }

    public CalculationRequest save(CalculationRequest calculationRequest) {
        return calculationRequestRepository.save(calculationRequest);
    }

    public List<CalculationRequest> findByOrderId(String orderId) {
        return calculationRequestRepository.findByOrderId(orderId);
    }

    public CalculationRequest startCalculationRequest(CalculationRequestBodyDTO calculationRequestBody) {
        CalculationRequest calculationRequest = buildCalculationRequest(calculationRequestBody);
        CalculationRequest createdRequest = calculationRequestRepository.save(calculationRequest);
        calculationServiceCoreClient.startPredictions(createdRequest);
        return createdRequest;
    }

    private CalculationRequest buildCalculationRequest(CalculationRequestBodyDTO calculationRequestBodyDTO) {
        Order order = orderMongoRepository.findById(calculationRequestBodyDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Order with id {0} does not exist!", calculationRequestBodyDTO.getOrderId())));
        return CalculationRequest.builder()
                .distance(calculationRequestBodyDTO.getDistance())
                .model(calculationRequestBodyDTO.getModel())
                .orderId(order.getId())
                .name(calculationRequestBodyDTO.getName())
                .createDate(OffsetDateTime.now(ZoneId.systemDefault()))
                .build();
    }

    public boolean removeByOrderId(String orderId) {
        return calculationRequestRepository.findAllByOrderId(orderId).stream()
                .allMatch(this::removeCalculationRequest);
    }

    private boolean removeCalculationRequest(CalculationRequest calculationRequest) {
        calculationRequestRepository.delete(calculationRequest);
        testDataMongoRepository.deleteAllByCalculationRequestId(calculationRequest.getId());
        satellitePredictionMongoRepository.deleteAllByCalculationRequestId(calculationRequest.getId());
        return true;
    }
}