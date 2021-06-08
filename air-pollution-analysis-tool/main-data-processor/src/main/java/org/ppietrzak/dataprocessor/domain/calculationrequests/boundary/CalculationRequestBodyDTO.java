package org.ppietrzak.dataprocessor.domain.calculationrequests.boundary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "CalculationRequestBodyDTOBuilder")
@JsonDeserialize(builder = CalculationRequestBodyDTO.CalculationRequestBodyDTOBuilder.class)
public class CalculationRequestBodyDTO {

    @NonNull
    private String name;

    @NonNull
    private String model;

    @NonNull
    private long distance;

    @NonNull
    private String orderId;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CalculationRequestBodyDTOBuilder {

    }
}
