package org.ppietrzak.dataprocessor.domain.satellitemeasurements.repository.mongo;

import org.ppietrzak.dataprocessor.domain.satellitemeasurements.model.SatelliteMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatelliteMeasurementsMongoRepository extends MongoRepository<SatelliteMeasurement, String> {

    Page<SatelliteMeasurement> findAllByOrderId(String orderId, Pageable pageable);

    void deleteAllByOrderId(String orderId);
}
