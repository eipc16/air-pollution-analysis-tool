package org.ppietrzak.dataprocessor.domain.orders.boundary;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderResource {

    private final OrderService orderService;

    @PostMapping
    @ResponseBody
    public Order createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.placeOrder(orderRequestDTO);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Order getOrderById(@PathVariable("id") String orderId) {
        return orderService.getOrderById(orderId);
    }
}
