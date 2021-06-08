package org.ppietrzak.dataprocessor.domain.calculationrequests.repository.mongo;

import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.repository.custom.CalculationRequestCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalculationRequestMongoRepository extends MongoRepository<CalculationRequest, String>, CalculationRequestCustomRepository {

    Optional<CalculationRequest> findById(String id);

    List<CalculationRequest> findAllByOrderId(String orderId);
}