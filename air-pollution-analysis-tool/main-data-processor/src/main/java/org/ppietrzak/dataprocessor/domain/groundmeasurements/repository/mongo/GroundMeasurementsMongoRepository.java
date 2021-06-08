package org.ppietrzak.dataprocessor.domain.groundmeasurements.repository.mongo;

import org.ppietrzak.dataprocessor.domain.groundmeasurements.model.GroundMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroundMeasurementsMongoRepository extends MongoRepository<GroundMeasurement, String> {

    Page<GroundMeasurement> findAllByOrderId(String orderId, Pageable pageable);

    void deleteAllByOrderId(String orderId);
}
