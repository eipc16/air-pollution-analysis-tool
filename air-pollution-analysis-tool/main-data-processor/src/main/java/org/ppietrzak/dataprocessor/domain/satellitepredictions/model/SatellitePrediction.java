package org.ppietrzak.dataprocessor.domain.satellitepredictions.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Builder(toBuilder = true)
@Getter
@Document(collection = "satellitePredictions")
public class SatellitePrediction {

    @Id
    private String id;

    @NonNull
    private String calculationRequestId;

    @NonNull
    private long day;

    @NonNull
    private long month;

    @NonNull
    private long year;

    @NonNull
    private long hour;

    @NonNull
    private long minute;

    @NonNull
    private double latitude;

    @NonNull
    private double longitude;

    @NonNull
    private double predictions;

    @NonNull
    private double value;
}
