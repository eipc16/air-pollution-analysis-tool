package org.ppietrzak.dataprocessor.domain.satellitemeasurements.mappers;

import org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary.SatelliteMeasurementDTO;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary.SatelliteMeasurementIndexationBatchDTO;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.model.SatelliteMeasurement;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ApplicationScope
public class SatelliteMeasurementDTOMapper {

    public List<SatelliteMeasurement> toEntities(SatelliteMeasurementIndexationBatchDTO indexationBatchDTO) {
        return indexationBatchDTO.getMeasurements().stream()
                .map(measurement -> toEntity(indexationBatchDTO.getOrderId(), measurement))
                .collect(Collectors.toList());
    }

    private SatelliteMeasurement toEntity(String orderId, SatelliteMeasurementDTO measurementDTO) {
        return SatelliteMeasurement.builder()
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
