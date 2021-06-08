package org.ppietrzak.grounddatacore.api.stations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

@Builder(builderClassName = "GroundDataBatchBuilder")
@Getter
@JsonDeserialize(builder = GroundStationIndexationBatchDTO.GroundDataBatchBuilder.class)
public class GroundStationIndexationBatchDTO {

    @NonNull
    private String source;

    @NonNull
    private long batchSize;

    @NonNull
    @Singular
    private List<GroundDataStationDTO> stations;

    @NonNull
    private long currentBatch;

    @NonNull
    private long totalBatches;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GroundDataBatchBuilder {
        // will be filled by lombok
    }
}
