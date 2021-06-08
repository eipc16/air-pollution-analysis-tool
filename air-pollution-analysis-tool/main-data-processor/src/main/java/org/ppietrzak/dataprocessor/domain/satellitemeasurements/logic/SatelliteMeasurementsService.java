package org.ppietrzak.dataprocessor.domain.satellitemeasurements.logic;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.model.GroundMeasurement;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.model.SatelliteMeasurement;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.repository.mongo.SatelliteMeasurementsMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SatelliteMeasurementsService {

    private final SatelliteMeasurementsMongoRepository mongoRepository;

    public List<SatelliteMeasurement> saveAll(Collection<SatelliteMeasurement> measurements) {
        return mongoRepository.saveAll(measurements);
    }

    public Page<SatelliteMeasurement> findAllByOrderId(String orderId, int page, int size) {
        return mongoRepository.findAllByOrderId(orderId, PageRequest.of(page, size));
    }
}
