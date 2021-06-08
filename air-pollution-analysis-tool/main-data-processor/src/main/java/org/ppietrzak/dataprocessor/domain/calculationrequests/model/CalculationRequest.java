package org.ppietrzak.dataprocessor.domain.calculationrequests.model;

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
@Document(collection = "calculationRequests")
public class CalculationRequest {

    @Id
    private String id;

    @NonNull
    private String orderId;

    @NonNull
    private String name;

    @NonNull
    private OffsetDateTime createDate;

    @NonNull
    private String model;

    @NonNull
    private long distance;
}
