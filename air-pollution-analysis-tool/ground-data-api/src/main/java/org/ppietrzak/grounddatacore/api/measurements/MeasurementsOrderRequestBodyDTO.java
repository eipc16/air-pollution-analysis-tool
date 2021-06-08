package org.ppietrzak.grounddatacore.api.measurements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

@Getter
@Builder(builderClassName = "MeasurementsOrderRequestBodyDTOBuilder")
@JsonDeserialize(builder = MeasurementsOrderRequestBodyDTO.MeasurementsOrderRequestBodyDTOBuilder.class)
public class MeasurementsOrderRequestBodyDTO {

    @JsonProperty("orderId")
    @NonNull
    private final String orderId;

    @JsonProperty("targetTopic")
    @NonNull
    private final String targetTopic;

    @JsonProperty("source")
    @NonNull
    private final String source;

    @JsonProperty("parameter")
    @NonNull
    private final String parameter;

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
    public static class MeasurementsOrderRequestBodyDTOBuilder {

        public MeasurementsOrderRequestBodyDTOBuilder dateFrom(OffsetDateTime dateFrom) {
            this.dateFrom = dateFrom.toString();
            return this;
        }

        public MeasurementsOrderRequestBodyDTOBuilder dateTo(OffsetDateTime dateTo) {
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