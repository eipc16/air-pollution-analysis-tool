package org.ppietrzak.grounddatacore.client.openaq.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(builder = OpenAQCoordinatesDTO.OpenAQCoordinatesDTOBuilder.class)
@Builder(builderClassName = "OpenAQCoordinatesDTOBuilder")
public class OpenAQCoordinatesDTO {

    private final double latitude;
    private final double longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQCoordinatesDTOBuilder {
        // will be filled by lombok
    }
}
