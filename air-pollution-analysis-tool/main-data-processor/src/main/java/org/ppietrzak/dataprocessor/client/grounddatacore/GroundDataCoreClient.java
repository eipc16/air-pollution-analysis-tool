package org.ppietrzak.dataprocessor.client.grounddatacore;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteSourceDTO;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteSourceParameterDTO;
import org.ppietrzak.dataprocessor.config.services.ServiceConfiguration;
import org.ppietrzak.dataprocessor.config.services.Services;
import org.ppietrzak.dataprocessor.config.services.ServicesConfigurationProperties;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.infrastructure.kafka.KafkaTopics;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsOrderRequestBodyDTO;
import org.ppietrzak.grounddatacore.api.parameters.GroundDataParameterDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.api.stations.GroundDataStationPageDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GroundDataCoreClient {

    private static final String GROUND_MEASUREMENTS_FETCH_PATH = "/measurements/fetch";
    private static final RestTemplate restTemplate = new RestTemplate();

    private final ServicesConfigurationProperties servicesConfigurationProperties;

    public String getBaseUrl() {
        ServiceConfiguration config = servicesConfigurationProperties.getServiceConfiguration(Services.GROUND_DATA_CORE);
        if(config.getPort() == null) {
            return UriComponentsBuilder.fromUriString(config.getHost()).build().toUriString();
        }
        return UriComponentsBuilder.fromUriString(config.getHost() + ":" + config.getPort()).build().toUriString();
    }

    public HttpStatus startFetchingGroundData(KafkaTopics targetTopic, Order order) {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path(GROUND_MEASUREMENTS_FETCH_PATH)
                .build()
                .toUriString();
        MeasurementsOrderRequestBodyDTO measurementsOrderRequestBodyDTO = MeasurementsOrderRequestBodyDTO.builder()
                .source(order.getGroundSource())
                .parameter(order.getGroundParameter())
                .targetTopic(targetTopic.getName())
                .orderId(order.getId())
                .dateFrom(order.getStartDate())
                .dateTo(order.getEndDate())
                .bottomLatitude(order.getBottomLatitude())
                .bottomLongitude(order.getBottomLongitude())
                .upperLatitude(order.getUpperLatitude())
                .upperLongitude(order.getUpperLongitude())
                .build();
        ResponseEntity<Object> response = restTemplate.postForEntity(
                fetchDataPath, measurementsOrderRequestBodyDTO, Object.class);
        return response.getStatusCode();
    }

    public List<GroundDataSourceDTO> getSources() {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path("sources")
                .build()
                .toUriString();
        return restTemplate.exchange(
                fetchDataPath,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GroundDataSourceDTO>>() {}).getBody();
    }

    public List<GroundDataParameterDTO> getSourceParameters(String source) {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path("/sources/")
                .path(source)
                .path("/parameters")
                .build()
                .toUriString();
        return restTemplate.exchange(
                fetchDataPath,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GroundDataParameterDTO>>() {}).getBody();
    }

    public GroundDataStationPageDTO getStations(String source, @Nullable String parameter,
                                                double bottomLatitude, double bottomLongitude,
                                                double upperLatitude, double upperLongitude,
                                                int page, int count) {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path("/stations/")
                .path(source)
                .queryParamIfPresent("parameter", Optional.ofNullable(parameter))
                .queryParam("bottomLatitude", bottomLatitude)
                .queryParam("upperLatitude", upperLatitude)
                .queryParam("bottomLongitude", bottomLongitude)
                .queryParam("upperLongitude", upperLongitude)
                .queryParam("page", page)
                .queryParam("size", count)
                .build()
                .toUriString();
        return restTemplate.exchange(
                fetchDataPath,
                HttpMethod.GET,
                null,
                GroundDataStationPageDTO.class).getBody();
    }
}
