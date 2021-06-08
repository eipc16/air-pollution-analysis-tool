package org.ppietrzak.dataprocessor.domain.orders.boundary;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersMutationResolver implements GraphQLMutationResolver {

    private final OrderService orderService;

    public Order createOrder(OrderRequestDTO orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    public boolean removeOrder(String orderId) {
        return orderService.removeOrder(orderId);
    }
}
