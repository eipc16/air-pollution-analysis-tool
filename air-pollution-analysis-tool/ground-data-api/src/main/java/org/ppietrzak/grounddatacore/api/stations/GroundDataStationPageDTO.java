package org.ppietrzak.grounddatacore.api.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "GroundDataStationPageDTOBuilder")
@JsonDeserialize(builder = GroundDataStationPageDTO.GroundDataStationPageDTOBuilder.class)
public class GroundDataStationPageDTO {

    @JsonProperty("count")
    @NonNull
    private long count;

    @JsonProperty("totalCount")
    @NonNull
    private long totalCount;

    @JsonProperty("page")
    @NonNull
    private int page;

    @JsonProperty("totalPages")
    @NonNull
    private int totalPages;

    @JsonProperty("content")
    @NonNull
    private List<GroundDataStationDTO> content;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GroundDataStationPageDTOBuilder {
        // will be filled by lombok
    }
}
