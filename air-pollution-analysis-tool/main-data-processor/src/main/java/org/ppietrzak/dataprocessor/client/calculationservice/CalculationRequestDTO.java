package org.ppietrzak.dataprocessor.client.calculationservice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "CalculationRequestDTOBuilder")
@JsonDeserialize(builder = CalculationRequestDTO.CalculationRequestDTOBuilder.class)
public class CalculationRequestDTO {

    @NonNull
    private String orderId;

    @NonNull
    private String calculationRequestId;

    @NonNull
    private String name;

    @NonNull
    private long distance;

    @NonNull
    private String model;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CalculationRequestDTOBuilder {
    }
}
