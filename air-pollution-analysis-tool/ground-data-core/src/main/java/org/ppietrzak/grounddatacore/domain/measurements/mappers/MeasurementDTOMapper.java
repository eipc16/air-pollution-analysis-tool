package org.ppietrzak.grounddatacore.domain.measurements.mappers;

import org.ppietrzak.grounddatacore.api.measurements.MeasurementDTO;
import org.ppietrzak.grounddatacore.domain.measurements.model.Measurement;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@Service
@ApplicationScope
public class MeasurementDTOMapper {

    public Measurement toEntity(MeasurementDTO measurementDTO) {
        return Measurement.builder()
                .locationId(measurementDTO.getLocationId())
                .location(measurementDTO.getLocationName())
                .latitude(measurementDTO.getLatitude())
                .longitude(measurementDTO.getLongitude())
                .measurementDate(measurementDTO.getMeasurementDate())
                .value(measurementDTO.getValue())
                .parameter(measurementDTO.getParameter())
                .id(String.format("%s_%s_%d", measurementDTO.getLocationName(), measurementDTO.getParameter(), measurementDTO.getMeasurementDate().toEpochSecond()))
                .build();
    }
}
