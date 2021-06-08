package org.ppietrzak.grounddatacore.api.sources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsOrderRequestBodyDTO;
import org.ppietrzak.grounddatacore.api.parameters.GroundDataParameterDTO;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Builder(builderClassName = "GroundDataBuilder")
@Getter
@JsonDeserialize(builder = GroundDataSourceDTO.GroundDataBuilder.class)
public class GroundDataSourceDTO {

    private String id;

    @NonNull
    private String name;

    @NonNull
    private String fullName;

    @NonNull
    private String dataUrl;

    @NonNull
    private String heartBeatUrl;

    @NonNull
    private String reindexUrl;

    @NonNull
    private String reindexStatusUrl;

    @JsonProperty("lastUpdated")
    private final OffsetDateTime lastUpdated;

    @NonNull
    @Singular
    private List<GroundDataParameterDTO> parameters;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate minDate;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate maxDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GroundDataBuilder {

//        public GroundDataBuilder lastUpdated(OffsetDateTime lastUpdated) {
//            this.lastUpdated = lastUpdated.toString();
//            return this;
//        }

        // will be filled by lombok
    }

//    public OffsetDateTime getLastUpdated() {
//        return OffsetDateTime.parse(lastUpdated);
//    }
}
