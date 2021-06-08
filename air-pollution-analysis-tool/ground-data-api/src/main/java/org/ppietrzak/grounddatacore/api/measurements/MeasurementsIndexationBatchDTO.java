package org.ppietrzak.grounddatacore.api.measurements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

@Builder(builderClassName = "MeasurementsIndexationBatchDTOBuilder")
@Getter
@JsonDeserialize(builder = MeasurementsIndexationBatchDTO.MeasurementsIndexationBatchDTOBuilder.class)
public class MeasurementsIndexationBatchDTO {

    @NonNull
    private String orderId;

    @NonNull
    @Singular
    private List<MeasurementDTO> measurements;

    @NonNull
    private long currentBatch;

    @NonNull
    private long totalBatches;

    @NonNull
    private int totalBatchParts;

    @NonNull
    private int batchPart;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MeasurementsIndexationBatchDTOBuilder {
        // will be filled by lombok
    }

    @JsonIgnore
    public boolean isLastBatch() {
        return currentBatch == totalBatches && batchPart == totalBatchParts;
    }
}
