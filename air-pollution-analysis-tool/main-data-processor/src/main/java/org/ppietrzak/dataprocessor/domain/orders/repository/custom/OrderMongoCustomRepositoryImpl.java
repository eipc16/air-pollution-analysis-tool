package org.ppietrzak.dataprocessor.domain.orders.repository.custom;

import lombok.AllArgsConstructor;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

@AllArgsConstructor
public class OrderMongoCustomRepositoryImpl implements OrderMongoCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Order> getOrdersWithCompleteAndNotCalculatedDataAndModelSpecified() {
        Query query = new Query()
                .addCriteria(Criteria.where("isGroundDataReady").is(true))
                .addCriteria(Criteria.where("isSatelliteDataReady").is(true))
                .addCriteria(Criteria.where("isInitialCalculationStarted").is(false))
                .addCriteria(Criteria.where("initialModel").exists(true));
        return mongoTemplate.find(query, Order.class);
    }
}
