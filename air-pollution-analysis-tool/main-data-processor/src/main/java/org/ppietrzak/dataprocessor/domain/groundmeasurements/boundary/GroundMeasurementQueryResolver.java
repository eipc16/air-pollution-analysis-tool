package org.ppietrzak.dataprocessor.domain.groundmeasurements.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.logic.GroundMeasurementsService;
import org.ppietrzak.dataprocessor.domain.groundmeasurements.model.GroundMeasurement;
import org.ppietrzak.dataprocessor.infrastructure.graphql.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroundMeasurementQueryResolver implements GraphQLQueryResolver {

    private final GroundMeasurementsService groundMeasurementsService;

    public Pagination<GroundMeasurement> getGroundData(String orderId, int page, int size) {
        Page<GroundMeasurement> measurementsPage = groundMeasurementsService.findByOrderId(orderId, page, size);
        return Pagination.from(measurementsPage);
    }
}
