package org.ppietrzak.grounddatacore.client.openaq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@JsonDeserialize(builder = OpenAQDate.OpenAQDateBuilder.class)
@Builder(builderClassName = "OpenAQDateBuilder")
public class OpenAQDate {

    @JsonProperty("utc")
    private final OffsetDateTime utc;

    @JsonProperty("local")
    private final OffsetDateTime local;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OpenAQDateBuilder {
        // will be filled by lombok
    }
}
