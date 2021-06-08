package org.ppietrzak.grounddatacore.domain.stations.logic;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.grounddatacore.client.openaq.OpenAQClient;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQGroundStationPaginatedResultDTO;
import org.ppietrzak.grounddatacore.client.openaq.entities.StationsMetaInfo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroundDataStationsService {

    private final OpenAQClient openAQClient;

    public StationsMetaInfo getStationsMetaInfo(int count) {
        return openAQClient.getStationsMetaInfo(count);
    }

    public OpenAQGroundStationPaginatedResultDTO getStations(int page, int count) {
        return openAQClient.getStations(page, count);
    }
}
