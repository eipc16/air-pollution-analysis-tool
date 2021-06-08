package org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.logic.SatelliteMeasurementsService;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.model.SatelliteMeasurement;
import org.ppietrzak.dataprocessor.infrastructure.graphql.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SatelliteMeasurementQueryResolver implements GraphQLQueryResolver {

    private final SatelliteMeasurementsService satelliteMeasurementsService;

    public Pagination<SatelliteMeasurement> getSatelliteData(String orderId, int page, int size) {
        Page<SatelliteMeasurement> satelliteMeasurements = satelliteMeasurementsService.findAllByOrderId(orderId, page, size);
        return Pagination.from(satelliteMeasurements);
    }
}
