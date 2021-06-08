package org.ppietrzak.dataprocessor.domain.orders.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.dataprocessor.client.calculationservice.CalculationServiceCoreClient;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.services.CalculationRequestService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class OrderCalculationScheduler {

    private final OrderService orderService;
    private final CalculationServiceCoreClient calculationServiceCoreClient;
    private final CalculationRequestService calculationRequestService;

    @Scheduled(fixedRate = 300000, initialDelay = 5000)
    public void scheduleCalculations() {
        List<Order> ordersToSchedule = orderService.getOrdersWithCompleteAndNotCalculatedDataAndModelSpecified();
        log.info("Found {} orders to schedule initial calculation for", ordersToSchedule.size());
        ordersToSchedule.forEach(this::startCalculation);
    }

    private void startCalculation(Order order) {
        Order updatedOrder = orderService.save(updateOrderData(order));
        CalculationRequest calculationRequest = calculationRequestService.save(prepareCalculationRequests(updatedOrder));
        calculationServiceCoreClient.startPredictions(calculationRequest);
    }

    private Order updateOrderData(Order order) {
        return order.toBuilder()
                .isInitialCalculationStarted(true)
                .build();
    }

    private CalculationRequest prepareCalculationRequests(Order order) {
        return CalculationRequest.builder()
                .orderId(order.getId())
                .name(order.getName())
                .model(order.getInitialModel())
                .createDate(order.getCreateDate())
                .distance(order.getDistance())
                .build();
    }

}
