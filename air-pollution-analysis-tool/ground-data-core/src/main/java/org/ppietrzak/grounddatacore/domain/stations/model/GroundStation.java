package org.ppietrzak.grounddatacore.domain.stations.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@ToString
@Builder(toBuilder = true)
@Getter
@Document(collection = "groundStations")
public class GroundStation {

    @NonNull
    @Id
    private String id;

    @NonNull
    private String source;

    @NonNull
    private String name;

    @NonNull
    private double latitude;

    @NonNull
    private double longitude;

    @NonNull
    @Singular
    private Set<String> parameters;
}
