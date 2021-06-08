package org.ppietrzak.dataprocessor.client.calculationservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteSourceDTO;

@Getter
@Builder(builderClassName = "ModelDTOBuilder")
@JsonDeserialize(builder = ModelDTO.ModelDTOBuilder.class)
public class ModelDTO {

    @JsonProperty("name")
    private String name;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ModelDTOBuilder {
        // will be filled by lombok
    }
}
