package org.ppietrzak.grounddatacore.client.sources;

import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.healthcheck.HealthCheckDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsPageDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsRequestBodyDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceIndexationStatusDTO;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.infrastructure.exceptions.StationsReindexationFailureException;
import org.ppietrzak.grounddatacore.infrastructure.exceptions.StationsReindexationStatusCheckFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@ApplicationScope
@Slf4j
public class SourceFetcherClient {

    private static RestTemplate restTemplate = new RestTemplate();

    public boolean isHealthy(GroundDataSource groundDataSource) {
        boolean isHealthy = false;
        try {
            ResponseEntity<HealthCheckDTO> response = restTemplate.getForEntity(groundDataSource.getHeartBeatUrl(), HealthCheckDTO.class);
            isHealthy = Optional.ofNullable(response.getBody()).map(HealthCheckDTO::isHealthy).orElse(response.getStatusCode().equals(HttpStatus.OK));
        } catch (RestClientException exception) {
            isHealthy = false;
            log.warn("Healthcheck for source {} failed", groundDataSource.getName(), exception);
        }
        return isHealthy;
    }

    public GroundDataSourceIndexationStatusDTO reindexStations(GroundDataSource groundDataSource) {
        try {
            RequestCallback requestCallback = restTemplate.httpEntityCallback(null);
            ResponseExtractor<ResponseEntity<GroundDataSourceIndexationStatusDTO>> responseExtractor =
                    restTemplate.responseEntityExtractor(GroundDataSourceIndexationStatusDTO.class);
            ResponseEntity<GroundDataSourceIndexationStatusDTO> response =
                    restTemplate.execute(groundDataSource.getReindexUrl(), HttpMethod.POST, requestCallback, responseExtractor);
            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new StationsReindexationFailureException(groundDataSource.getName(), "Excepted indexation status in response body."));
        } catch (RestClientException exception) {
            log.warn("Healthcheck for source {} failed", groundDataSource.getName(), exception);
            throw new StationsReindexationFailureException(groundDataSource.getName(), exception);
        }
    }

    public Optional<GroundDataSourceIndexationStatusDTO> getIndexationStatus(GroundDataSource groundDataSource) {
        Optional<GroundDataSourceIndexationStatusDTO> status;
        try {
            ResponseEntity<GroundDataSourceIndexationStatusDTO> response =
                    restTemplate.getForEntity(groundDataSource.getReindexStatusUrl(), GroundDataSourceIndexationStatusDTO.class);
            status = Optional.ofNullable(response.getBody());
        } catch (RestClientException exception) {
            status = Optional.empty();
//            throw new StationsReindexationStatusCheckFailureException(groundDataSource.getName(), exception);
        }
        return status;
    }

    public MeasurementsPageDTO getMeasurements(GroundDataSource source, List<Long> stationIds, String parameter,
                                               OffsetDateTime from, OffsetDateTime to,
                                               int page, int count) {
        MeasurementsRequestBodyDTO requestBody = MeasurementsRequestBodyDTO.builder()
                .stations(stationIds)
                .parameter(parameter)
                .dateFrom(from)
                .dateTo(to)
                .build();
        ResponseEntity<MeasurementsPageDTO> response = restTemplate.postForEntity(
                source.getDataUrl(page, count),
                requestBody,
                MeasurementsPageDTO.class
        );
        return response.getBody();
    }
}
