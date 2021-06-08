package org.ppietrzak.grounddatacore.domain.sources.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ToString
@Builder(toBuilder = true)
@Getter
@Document(collection = "dataSources")
@RedisHash("GroundDataSource")
public class GroundDataSource {

    @NonNull
    @Id
    private String name;

    private String id;

    @NonNull
    private String fullName;

    @NonNull
    private String dataUrl;

    @NonNull
    private String heartBeatUrl;

    @NonNull
    private String reindexUrl;

    @NonNull
    private String reindexStatusUrl;

    @Singular
    private List<GroundDataParameter> parameters;

    @NonNull
    private LocalDate minDate;

    @NonNull
    private LocalDate maxDate;

    private OffsetDateTime lastUpdated;

    public List<GroundDataParameter> getParameters() {
        return Optional.ofNullable(parameters).orElseGet(Collections::emptyList);
    }

    public String getDataUrl(int page, int count) {
        return UriComponentsBuilder.fromUriString(dataUrl)
                .queryParam("page", page)
                .queryParam("size", count)
                .build()
                .toUriString();
    }
}
