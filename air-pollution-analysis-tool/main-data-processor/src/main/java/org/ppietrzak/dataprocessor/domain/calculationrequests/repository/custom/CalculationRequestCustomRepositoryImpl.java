package org.ppietrzak.dataprocessor.domain.calculationrequests.repository.custom;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class CalculationRequestCustomRepositoryImpl implements CalculationRequestCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<CalculationRequest> findByOrderId(String orderId) {
        Query query = new Query()
                .addCriteria(Criteria.where("orderId").is(orderId));
        return mongoTemplate.find(query, CalculationRequest.class);
    }
}
