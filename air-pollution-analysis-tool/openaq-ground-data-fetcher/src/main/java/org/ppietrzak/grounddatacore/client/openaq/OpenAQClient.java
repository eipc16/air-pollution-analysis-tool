package org.ppietrzak.grounddatacore.client.openaq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQGroundStationPaginatedResultDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQMeasurementPaginatedResultDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQPaginatedResultMetaDTO;
import org.ppietrzak.grounddatacore.client.openaq.entities.StationsMetaInfo;
import org.ppietrzak.grounddatacore.config.openaq.OpenAQConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@ApplicationScope
@RequiredArgsConstructor
@Slf4j
public class OpenAQClient {

    private final static RestTemplate restTemplate = new RestTemplate();

    private final Duration sleepTime = Duration.ofMinutes(2);

    private final OpenAQConfigurationProperties openAQConfigurationProperties;

    public StationsMetaInfo getStationsMetaInfo(int count) {
        OpenAQPaginatedResultMetaDTO meta = getStations(1, 1).getMeta();
        return StationsMetaInfo.builder()
                .totalPages((int) Math.ceil(meta.getFound() / (count * 1.0)))
                .totalStations(meta.getFound())
                .count(count)
                .build();
    }

    public OpenAQGroundStationPaginatedResultDTO getStations(int page, int count) {
        ResponseEntity<OpenAQGroundStationPaginatedResultDTO> response = fetchStations(page, count);
        OpenAQGroundStationPaginatedResultDTO result = response.getBody();
        if (result == null) {
            throw new RuntimeException("No result");
        }
        return result;
    }

    private ResponseEntity<OpenAQGroundStationPaginatedResultDTO> fetchStations(int page, int count) {
        try {
            String requestUrl = openAQConfigurationProperties.getLocationsUrl(page, count);
            return restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    null,
                    OpenAQGroundStationPaginatedResultDTO.class);
        } catch (HttpClientErrorException ex) {
            if (HttpStatus.FORBIDDEN.equals(ex.getStatusCode())) {
                log.warn("Reached OpenAQ Limit. Waiting for {}.", sleepTime.toString());
                try {
                    Thread.sleep(sleepTime.toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return fetchStations(page, count);
            }
        }
        throw new RuntimeException("Couldn't fetch data.");
    }

    public OpenAQMeasurementPaginatedResultDTO getMeasurements(List<Long> stationIds,
                                                               String parameter,
                                                               OffsetDateTime from, OffsetDateTime to,
                                                               int page, int count) {
        ResponseEntity<OpenAQMeasurementPaginatedResultDTO> response = fetchMeasurements(stationIds, parameter, from, to, page + 1, count);
        OpenAQMeasurementPaginatedResultDTO result = response.getBody();
        if (result == null) {
            throw new RuntimeException("No result");
        }
        return result;
    }

    private ResponseEntity<OpenAQMeasurementPaginatedResultDTO> fetchMeasurements(List<Long> stationIds,
                                                                                  String parameter,
                                                                                  OffsetDateTime from, OffsetDateTime to,
                                                                                  int page, int count) {
        try {
            String requestUrl = openAQConfigurationProperties.getMeasurementsUrl(stationIds, parameter, from, to, page, count);
            return restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    null,
                    OpenAQMeasurementPaginatedResultDTO.class);
        } catch (HttpClientErrorException ex) {
            if (HttpStatus.FORBIDDEN.equals(ex.getStatusCode()) || HttpStatus.SERVICE_UNAVAILABLE.equals(ex.getStatusCode())) {
                log.warn("Reached OpenAQ Limit. Waiting for {}.", sleepTime.toString());
                try {
                    Thread.sleep(sleepTime.toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return fetchMeasurements(stationIds, parameter, from, to, page, count);
            }
        }
        throw new RuntimeException("Couldn't fetch data.");
    }
}
