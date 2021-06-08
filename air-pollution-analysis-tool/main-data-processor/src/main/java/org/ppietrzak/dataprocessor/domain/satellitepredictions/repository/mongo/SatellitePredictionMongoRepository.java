package org.ppietrzak.dataprocessor.domain.satellitepredictions.repository.mongo;

import org.ppietrzak.dataprocessor.domain.satellitepredictions.model.SatellitePrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatellitePredictionMongoRepository extends MongoRepository<SatellitePrediction, String> {

    Page<SatellitePrediction> findAllByCalculationRequestId(String calculationRequestId, Pageable pageable);

    void deleteAllByCalculationRequestId(String calculationRequestId);
}