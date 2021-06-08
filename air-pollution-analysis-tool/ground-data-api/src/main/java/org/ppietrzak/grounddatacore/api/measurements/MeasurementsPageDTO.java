package org.ppietrzak.grounddatacore.api.measurements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "MeasurementsPageDTOBuilder")
@JsonDeserialize(builder = MeasurementsPageDTO.MeasurementsPageDTOBuilder.class)
public class MeasurementsPageDTO {

    @NonNull
    @Singular
    private List<MeasurementDTO> measurements;

    @NonNull
    private long count;

    @NonNull
    private long totalCount;

    @NonNull
    private long currentPage;

    @NonNull
    private long totalPages;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MeasurementsPageDTOBuilder {
        // will be filled by lombok
    }

    @JsonIgnore
    public boolean hasNextPage() {
        return currentPage + 1 < totalPages;
    }
}
