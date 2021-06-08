package org.ppietrzak.dataprocessor.domain.groundmeasurements.logic;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.model.GroundMeasurement;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.repository.mongo.GroundMeasurementsMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroundMeasurementsService {

    private final GroundMeasurementsMongoRepository groundMeasurementsRepository;

    public List<GroundMeasurement> saveAll(Collection<GroundMeasurement> groundMeasurements) {
        return groundMeasurementsRepository.saveAll(groundMeasurements);
    }

    public Page<GroundMeasurement> findByOrderId(String orderId, int page, int size) {
        return groundMeasurementsRepository.findAllByOrderId(orderId, PageRequest.of(page, size));
    }
}
