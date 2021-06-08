package org.ppietrzak.dataprocessor.domain.orders.repository.custom;

import org.ppietrzak.dataprocessor.domain.orders.model.Order;

import java.util.List;

public interface OrderMongoCustomRepository {

    List<Order> getOrdersWithCompleteAndNotCalculatedDataAndModelSpecified();
}
