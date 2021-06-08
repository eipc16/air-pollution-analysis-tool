package org.ppietrzak.dataprocessor.domain.orders.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.infrastructure.graphql.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersQueryResolver implements GraphQLQueryResolver {

    private final OrderService orderService;

    public Pagination<Order> orders(int page, int size) {
        Page<Order> orders = orderService.findAll(page, size);
        return Pagination.from(orders);
    }

    public Order order(String id) {
        return orderService.getOrderById(id);
    }
}
