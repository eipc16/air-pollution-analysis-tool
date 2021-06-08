package org.ppietrzak.grounddatacore.api.stations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.util.Set;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "GroundDataStationDTOBuilder")
@JsonDeserialize(builder = GroundDataStationDTO.GroundDataStationDTOBuilder.class)
public class GroundDataStationDTO {

    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private double latitude;

    @NonNull
    private double longitude;

    @NonNull
    @Singular
    private Set<String> parameters;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GroundDataStationDTOBuilder {
        // will be filled by lombok
    }
}
