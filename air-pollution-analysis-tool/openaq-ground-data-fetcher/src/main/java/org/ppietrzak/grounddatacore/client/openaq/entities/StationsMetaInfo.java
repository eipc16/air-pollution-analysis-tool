package org.ppietrzak.grounddatacore.client.openaq.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class StationsMetaInfo {

    private final int totalPages;
    private final int count ;
    private final long totalStations;
}
