package org.ppietrzak.grounddatacore.client.openaq.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonDeserialize(builder = OpenAQGroundStationDTO.OpenAQGroundStationDTOBuilder.class)
@Builder(builderClassName = "OpenAQGroundStationDTOBuilder")
public class OpenAQGroundStationDTO {

    private final long id;
    private final String name;
    private final String entity;
    private final String country;
    //    private final OpenAQSourcesDTO sources;
    private final boolean isMobile;
    private final boolean isAnalysis;
    private final List<OpenAQParameterDTO> parameters;
    private final String sensorType;
    private final OpenAQCoordinatesDTO coordinates;
    private final String lastUpdated;
    private final String firstUpdated;
    private final long measurements;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQGroundStationDTOBuilder {
        // will be filled by lombok
    }
}
