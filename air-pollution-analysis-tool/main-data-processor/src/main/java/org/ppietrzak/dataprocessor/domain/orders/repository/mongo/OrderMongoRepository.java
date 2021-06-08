package org.ppietrzak.dataprocessor.domain.orders.repository.mongo;

import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.orders.repository.custom.OrderMongoCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMongoRepository extends MongoRepository<Order, String>, OrderMongoCustomRepository {
}
