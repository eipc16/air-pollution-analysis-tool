package org.ppietrzak.grounddatacore.client.openaq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(builder = OpenAQPaginatedResultMetaDTO.OpenAQPaginatedResultMetaDTOBuilder.class)
@Builder(builderClassName = "OpenAQPaginatedResultMetaDTOBuilder")
public class OpenAQPaginatedResultMetaDTO {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("license")
    private final String license;

    @JsonProperty("webiste")
    private final String website;

    @JsonProperty("page")
    private final long page;

    @JsonProperty("limit")
    private final long limit;

    @JsonProperty("found")
    private final long found;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQPaginatedResultMetaDTOBuilder {
        // will be filled by lombok
    }
}