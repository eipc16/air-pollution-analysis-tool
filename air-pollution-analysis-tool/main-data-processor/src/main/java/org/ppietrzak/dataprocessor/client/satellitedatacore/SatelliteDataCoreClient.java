package org.ppietrzak.dataprocessor.client.satellitedatacore;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.config.services.ServiceConfiguration;
import org.ppietrzak.dataprocessor.config.services.Services;
import org.ppietrzak.dataprocessor.config.services.ServicesConfigurationProperties;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.infrastructure.kafka.KafkaTopics;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsOrderRequestBodyDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SatelliteDataCoreClient {

    private static final String MEASUREMENTS_FETCH_PATH = "/measurements";
    private static final RestTemplate restTemplate = new RestTemplate();

    private final ServicesConfigurationProperties servicesConfigurationProperties;

    public String getBaseUrl() {
        ServiceConfiguration config = servicesConfigurationProperties.getServiceConfiguration(Services.SATELLITE_DATA_CORE);
        if(config.getPort() == null) {
            return UriComponentsBuilder.fromUriString(config.getHost()).build().toUriString();
        }
        return UriComponentsBuilder.fromUriString(config.getHost() + ":" + config.getPort()).build().toUriString();
    }

    public HttpStatus startFetchingGroundData(KafkaTopics targetTopic, Order order) {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path(MEASUREMENTS_FETCH_PATH)
                .build()
                .toUriString();
        SatelliteMeasurementsRequestBodyDTO measurementsOrderRequestBodyDTO = SatelliteMeasurementsRequestBodyDTO.builder()
                .source(order.getSatelliteSource())
                .parameter(order.getSatelliteParameter())
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

    public List<SatelliteSourceDTO> getSources() {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path("/sources")
                .build()
                .toUriString();
        return restTemplate.exchange(
                fetchDataPath,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SatelliteSourceDTO>>() {}).getBody();
    }

    public List<SatelliteSourceParameterDTO> getSourceParameters(String source) {
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
                new ParameterizedTypeReference<List<SatelliteSourceParameterDTO>>() {}).getBody();
    }
}
