package org.ppietrzak.grounddatacore.api.sources;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.time.LocalDateTime;

@Builder(toBuilder = true, builderClassName = "GroundDataSourceIndexationStatusDTOBuilder")
@JsonDeserialize(builder = GroundDataSourceIndexationStatusDTO.GroundDataSourceIndexationStatusDTOBuilder.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroundDataSourceIndexationStatusDTO {

    @NonNull
    private long indexedObjects;

    @NonNull
    private long totalObjects;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NonNull
    private Status status;

    private String errorCause;

    public enum Status {
        FAILED,
        PENDING,
        FINISHED,
        NOT_STARTED
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class GroundDataSourceIndexationStatusDTOBuilder {
        // will be filled by lombok
    }
}
