package org.ppietrzak.dataprocessor.domain.satellitepredictions.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.logic.SatellitePredictionService;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.model.SatellitePrediction;
import org.ppietrzak.dataprocessor.infrastructure.graphql.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SatellitePredictionQueryResolver implements GraphQLQueryResolver {

    private final SatellitePredictionService satellitePredictionService;

    public Pagination<SatellitePrediction> getPredictions(String calculationRequestId, int page, int size) {
        Page<SatellitePrediction> testData = satellitePredictionService.getByCalculationRequestId(calculationRequestId, page, size);
        return Pagination.from(testData);
    }
}