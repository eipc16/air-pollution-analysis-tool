package org.ppietrzak.dataprocessor.domain.groundmeasurements.mappers;

import org.ppietrzak.dataprocessor.domain.groundmeasurements.model.GroundMeasurement;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsIndexationBatchDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ApplicationScope
public class GroundMeasurementDTOMapper {

    public List<GroundMeasurement> toEntities(MeasurementsIndexationBatchDTO indexationBatchDTO) {
        return indexationBatchDTO.getMeasurements().stream()
                .map(measurement -> toEntity(indexationBatchDTO.getOrderId(), measurement))
                .collect(Collectors.toList());
    }

    private GroundMeasurement toEntity(String orderId, MeasurementDTO measurementDTO) {
        return GroundMeasurement.builder()
                .location(measurementDTO.getLocationName())
                .parameter(measurementDTO.getParameter())
                .orderId(orderId)
                .value(measurementDTO.getValue())
                .latitude(measurementDTO.getLatitude())
                .longitude(measurementDTO.getLongitude())
                .year(measurementDTO.getMeasurementDate().getYear())
                .month(measurementDTO.getMeasurementDate().getMonthValue())
                .day(measurementDTO.getMeasurementDate().getDayOfMonth())
                .hour(measurementDTO.getMeasurementDate().getHour())
                .minute(measurementDTO.getMeasurementDate().getMinute())
                .measurementDate(measurementDTO.getMeasurementDate())
                .build();
    }
}
