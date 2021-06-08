package org.ppietrzak.grounddatacore.api.stations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "GroundDataStationDTOBuilder")
@JsonDeserialize(builder = GroundDataStationDTO.GroundDataStationDTOBuilder.class)
public class GroundDataStationsDTO {

    @NonNull
    private String source;

    @NonNull
    @Singular
    private List<GroundDataStationDTO> stations;

    @NonNull
    private long count;

    @NonNull
    private long totalCount;

    @NonNull
    private long currentPage;

    @NonNull
    private long totalPages;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GroundDataStationDTOBuilder {
        // will be filled by lombok
    }
}
