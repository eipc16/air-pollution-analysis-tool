package org.ppietrzak.dataprocessor.client.calculationservice;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.config.services.ServiceConfiguration;
import org.ppietrzak.dataprocessor.config.services.Services;
import org.ppietrzak.dataprocessor.config.services.ServicesConfigurationProperties;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
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
public class CalculationServiceCoreClient {

    private static final String PREDICTIONS_PATH = "/predictions";
    private static final RestTemplate restTemplate = new RestTemplate();

    private final ServicesConfigurationProperties servicesConfigurationProperties;

    public String getBaseUrl() {
        ServiceConfiguration config = servicesConfigurationProperties.getServiceConfiguration(Services.CALCULATION_SERVICE_CORE);
        if(config.getPort() == null) {
            return UriComponentsBuilder.fromUriString(config.getHost()).build().toUriString();
        }
        return UriComponentsBuilder.fromUriString(config.getHost() + ":" + config.getPort()).build().toUriString();
    }

    public HttpStatus startPredictions(CalculationRequest calculationRequest) {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path(PREDICTIONS_PATH)
                .build()
                .toUriString();
        CalculationRequestDTO calculationRequestDTO = CalculationRequestDTO.builder()
                .orderId(calculationRequest.getOrderId())
                .name(calculationRequest.getName())
                .distance(calculationRequest.getDistance())
                .model(calculationRequest.getModel())
                .calculationRequestId(calculationRequest.getId())
                .build();
        ResponseEntity<Object> response = restTemplate.postForEntity(
                fetchDataPath, calculationRequestDTO, Object.class);
        return response.getStatusCode();
    }

    public List<ModelDTO> getModels() {
        String fetchDataPath = UriComponentsBuilder.fromUriString(getBaseUrl())
                .path("models")
                .build()
                .toUriString();
        return restTemplate.exchange(
                fetchDataPath,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ModelDTO>>() {}).getBody();
    }
}
