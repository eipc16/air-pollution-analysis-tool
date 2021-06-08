package org.ppietrzak.grounddatacore.api.measurements;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.List;

//@AllArgsConstructor
@Getter
@Builder(builderClassName = "MeasurementsRequestBodyDTOBuilder")
@JsonDeserialize(builder = MeasurementsRequestBodyDTO.MeasurementsRequestBodyDTOBuilder.class)
public class MeasurementsRequestBodyDTO {

    @JsonProperty("stationIds")
    @NonNull
    private final List<Long> stations;

    @JsonProperty("parameter")
    @NonNull
    private final String parameter;

    @JsonProperty("dateFrom")
    @NonNull
    private final String dateFrom;

    @JsonProperty("dateTo")
    @NonNull
    private final String dateTo;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MeasurementsRequestBodyDTOBuilder {

        public MeasurementsRequestBodyDTOBuilder dateFrom(OffsetDateTime dateFrom) {
            this.dateFrom = dateFrom.toString();
            return this;
        }

        public MeasurementsRequestBodyDTOBuilder dateTo(OffsetDateTime dateTo) {
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
