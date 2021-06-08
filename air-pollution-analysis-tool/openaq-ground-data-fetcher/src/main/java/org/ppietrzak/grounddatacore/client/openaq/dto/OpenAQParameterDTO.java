package org.ppietrzak.grounddatacore.client.openaq.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(builder = OpenAQParameterDTO.OpenAQParameterDTOBuilder.class)
@Builder(builderClassName = "OpenAQParameterDTOBuilder")
public class OpenAQParameterDTO {

    private final long id;
    private final String unit;
    private final long count;
    private final double average;
    private final double lastValue;
    private final String parameter;
    private final String displayName;
    private final String lastUpdated;
    private final long parameterId;
    private final String firstUpdated;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQParameterDTOBuilder {
        // will be filled by lombok
    }
}
