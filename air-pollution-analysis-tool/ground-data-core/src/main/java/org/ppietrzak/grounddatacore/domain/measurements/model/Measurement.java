package org.ppietrzak.grounddatacore.domain.measurements.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class Measurement {

    private final String id;
    private final long locationId;
    private final String location;
    private final String parameter;
    private final double value;
    private final double latitude;
    private final double longitude;
    private final OffsetDateTime measurementDate;

}
