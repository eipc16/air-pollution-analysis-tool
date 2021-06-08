package org.ppietrzak.grounddatacore.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementDTO;

//@Getter
@AllArgsConstructor
@Builder(builderClassName = "HealthCheckDTOBuilder")
@JsonDeserialize(builder = HealthCheckDTO.HealthCheckDTOBuilder.class)
public class HealthCheckDTO {

    @NonNull
    @JsonProperty("isHealthy")
    private boolean isHealthy;

    @JsonPOJOBuilder(withPrefix = "")
    public static class HealthCheckDTOBuilder {
        // will be filled by lombok
    }

    @JsonIgnore
    public boolean isHealthy() {
        return isHealthy;
    }
}
