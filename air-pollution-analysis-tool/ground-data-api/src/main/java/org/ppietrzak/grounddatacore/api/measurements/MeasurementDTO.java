package org.ppietrzak.grounddatacore.api.measurements;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "MeasurementDTOBuilder")
@JsonDeserialize(builder = MeasurementDTO.MeasurementDTOBuilder.class)
public class MeasurementDTO {

    @JsonProperty("locationId")
    private final long locationId;

    @JsonProperty("location")
    private final String locationName;

    @JsonProperty("parameter")
    private final String parameter;

    @JsonProperty("value")
    private final double value;

    @JsonProperty("latitude")
    private final double latitude;

    @JsonProperty("longitude")
    private final double longitude;

    @JsonProperty("date")
    private final OffsetDateTime measurementDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MeasurementDTOBuilder {
        // will be filled by lombok
    }
}
