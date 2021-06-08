package org.ppietrzak.dataprocessor.domain.orders.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@ToString
@Builder(toBuilder = true)
@Getter
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String userId;

    private String initialModel;

    @NonNull
    private long distance;

    @NonNull
    private boolean isGroundDataReady = false;

    @NonNull
    private boolean isSatelliteDataReady = false;

    @NonNull
    private boolean isInitialCalculationStarted = false;

    @NonNull
    private String groundSource;

    @NonNull
    private String groundParameter;

    @NonNull
    private String satelliteSource;

    @NonNull
    private String satelliteParameter;

    @NonNull
    private OffsetDateTime createDate;

    private OffsetDateTime lastUpdatedDate;

    @NonNull
    private OffsetDateTime startDate;

    @NonNull
    private OffsetDateTime endDate;

    @NonNull
    private double bottomLatitude;

    @NonNull
    private double bottomLongitude;

    @NonNull
    private double upperLatitude;

    @NonNull
    private double upperLongitude;
}
