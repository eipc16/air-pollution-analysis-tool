package org.ppietrzak.dataprocessor.domain.satellitepredictions.logic;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.model.SatellitePrediction;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.repository.mongo.SatellitePredictionMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SatellitePredictionService {

    private final SatellitePredictionMongoRepository satellitePredictionRepository;

    public Page<SatellitePrediction> getByCalculationRequestId(String calculationOrderId, int page, int size) {
        return satellitePredictionRepository.findAllByCalculationRequestId(calculationOrderId, PageRequest.of(page, size));
    }
}
