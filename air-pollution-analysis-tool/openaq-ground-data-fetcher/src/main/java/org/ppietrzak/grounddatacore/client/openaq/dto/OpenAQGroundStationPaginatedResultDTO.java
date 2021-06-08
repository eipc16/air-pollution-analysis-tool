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
@JsonDeserialize(builder = OpenAQGroundStationPaginatedResultDTO.OpenAQGroundStationPaginatedResultDTOBuilder.class)
@Builder(builderClassName = "OpenAQGroundStationPaginatedResultDTOBuilder")
public class OpenAQGroundStationPaginatedResultDTO {

    @JsonProperty("meta")
    private final OpenAQPaginatedResultMetaDTO meta;

    @JsonProperty("results")
    private final List<OpenAQGroundStationDTO> content;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQGroundStationPaginatedResultDTOBuilder {
        // will be filled by lombok
    }
}
