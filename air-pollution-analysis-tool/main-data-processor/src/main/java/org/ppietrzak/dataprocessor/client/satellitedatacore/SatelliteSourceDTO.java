package org.ppietrzak.dataprocessor.client.satellitedatacore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "SatelliteSourceDTOBuilder")
@JsonDeserialize(builder = SatelliteSourceDTO.SatelliteSourceDTOBuilder.class)
public class SatelliteSourceDTO {

    @JsonProperty("name")
    private String name;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SatelliteSourceDTOBuilder {
        // will be filled by lombok
    }
}
