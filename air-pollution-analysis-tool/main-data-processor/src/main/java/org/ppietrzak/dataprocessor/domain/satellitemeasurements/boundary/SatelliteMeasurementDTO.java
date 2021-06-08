package org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementDTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "SatelliteMeasurementDTOBuilder")
@JsonDeserialize(builder = SatelliteMeasurementDTO.SatelliteMeasurementDTOBuilder.class)
public class SatelliteMeasurementDTO {

    @JsonProperty("parameter")
    private final String parameter;

    @JsonProperty("value")
    private final double value;

    @JsonProperty("latitude")
    private final double latitude;

    @JsonProperty("longitude")
    private final double longitude;

    @JsonProperty("date")
    private final String measurementDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SatelliteMeasurementDTOBuilder {
        // will be filled by lombok
    }

    @JsonIgnore
    public OffsetDateTime getMeasurementDate() {
        return LocalDateTime.parse(measurementDate).atOffset(ZoneOffset.UTC);
    }
}
