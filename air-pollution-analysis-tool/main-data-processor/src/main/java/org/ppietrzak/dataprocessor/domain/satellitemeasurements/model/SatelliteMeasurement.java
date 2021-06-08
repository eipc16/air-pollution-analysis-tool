package org.ppietrzak.dataprocessor.domain.satellitemeasurements.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@ToString
@Builder(toBuilder = true)
@Getter
@Document(collection = "satelliteMeasurements")
public class SatelliteMeasurement {

    @Id
    private String id;

    private final String parameter;

    private final String orderId;

    private final double value;

    private final double latitude;

    private final double longitude;

    private final int year;

    private final int month;

    private final int day;

    private final int hour;

    private final int minute;

    private final OffsetDateTime measurementDate;
}
