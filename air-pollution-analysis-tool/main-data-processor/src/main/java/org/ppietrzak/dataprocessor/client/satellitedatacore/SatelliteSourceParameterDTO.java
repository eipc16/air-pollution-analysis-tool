package org.ppietrzak.dataprocessor.client.satellitedatacore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "SatelliteSourceParameterDTOBuilder")
@JsonDeserialize(builder = SatelliteSourceParameterDTO.SatelliteSourceParameterDTOBuilder.class)
public class SatelliteSourceParameterDTO {

    @JsonProperty("name")
    private String name;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SatelliteSourceParameterDTOBuilder {
        // will be filled by lombok
    }
}
