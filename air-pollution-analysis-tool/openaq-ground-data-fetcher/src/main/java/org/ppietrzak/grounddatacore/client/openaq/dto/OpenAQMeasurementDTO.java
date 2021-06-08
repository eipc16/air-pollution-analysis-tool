package org.ppietrzak.grounddatacore.client.openaq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonDeserialize(builder = OpenAQMeasurementDTO.OpenAQMeasurementDTOBuilder.class)
@Builder(builderClassName = "OpenAQMeasurementDTOBuilder")
public class OpenAQMeasurementDTO {

    @JsonProperty("locationId")
    private final long locationId;

    @JsonProperty("location")
    private final String locationName;

    @JsonProperty("parameter")
    private final String parameter;

    @JsonProperty("value")
    private final double value;

    @JsonProperty("coordinates")
    private final OpenAQCoordinatesDTO coordinates;

    @JsonProperty("date")
    private final OpenAQDate openAQDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQMeasurementDTOBuilder {
        // will be filled by lombok
    }
}
