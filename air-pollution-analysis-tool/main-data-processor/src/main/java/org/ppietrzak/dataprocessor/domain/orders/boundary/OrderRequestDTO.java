package org.ppietrzak.dataprocessor.domain.orders.boundary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

@Getter
@Builder(builderClassName = "OrderRequestDTOBuilder")
@JsonDeserialize(builder = OrderRequestDTO.OrderRequestDTOBuilder.class)
public class OrderRequestDTO {

    @JsonProperty("name")
    @NonNull
    private final String name;

    @JsonProperty("groundSource")
    @NonNull
    private final String groundSource;

    @JsonProperty("groundParameter")
    @NonNull
    private final String groundParameter;

    @JsonProperty("satelliteSource")
    @NonNull
    private final String satelliteSource;

    @JsonProperty("satelliteParameter")
    @NonNull
    private final String satelliteParameter;

    @JsonProperty("model")
    private final String model;

    @JsonProperty("distance")
    private final long distance;

    @JsonProperty("dateFrom")
    @NonNull
    private final String dateFrom;

    @JsonProperty("dateTo")
    @NonNull
    private final String dateTo;

    @JsonProperty("bottomLatitude")
    @NonNull
    private final double bottomLatitude;

    @JsonProperty("upperLatitude")
    @NonNull
    private final double upperLatitude;

    @JsonProperty("bottomLongitude")
    @NonNull
    private final double bottomLongitude;

    @JsonProperty("upperLongitude")
    @NonNull
    private final double upperLongitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OrderRequestDTOBuilder {

        public OrderRequestDTOBuilder dateFrom(OffsetDateTime dateFrom) {
            this.dateFrom = dateFrom.toString();
            return this;
        }

        public OrderRequestDTOBuilder dateTo(OffsetDateTime dateTo) {
            this.dateTo = dateTo.toString();
            return this;
        }

        // will be filled by lombok
    }

    @JsonIgnore
    public OffsetDateTime getDateFromAsOffsetDateTime() {
        return OffsetDateTime.parse(dateFrom);
    }

    @JsonIgnore
    public OffsetDateTime getDateToAsOffsetDateTime() {
        return OffsetDateTime.parse(dateTo);
    }
}