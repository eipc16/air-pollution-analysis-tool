package org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

@Builder(builderClassName = "SatelliteMeasurementIndexationBatchDTOBuilder")
@Getter
@JsonDeserialize(builder = SatelliteMeasurementIndexationBatchDTO.SatelliteMeasurementIndexationBatchDTOBuilder.class)
public class SatelliteMeasurementIndexationBatchDTO {

    @NonNull
    private String orderId;

    @NonNull
    @Singular
    private List<SatelliteMeasurementDTO> measurements;

    @NonNull
    private long currentBatch;

    @NonNull
    private long totalBatches;

    @NonNull
    private long currentBatchChunk;

    @NonNull
    private long currentBatchTotalChunks;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SatelliteMeasurementIndexationBatchDTOBuilder {
        // will be filled by lombok
    }

    @JsonIgnore
    public boolean isLastBatch() {
        return currentBatch == totalBatches && currentBatchChunk == currentBatchTotalChunks;
    }
}