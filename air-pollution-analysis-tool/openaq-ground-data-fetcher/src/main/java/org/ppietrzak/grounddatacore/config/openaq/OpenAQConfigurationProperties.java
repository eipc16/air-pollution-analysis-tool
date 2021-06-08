package org.ppietrzak.grounddatacore.config.openaq;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@ConfigurationProperties(prefix = "configuration.openaq")
@Setter
public class OpenAQConfigurationProperties {

    @Getter
    private String baseUrl;
    private OpenAQEndpoints endpoints;

    private static final String LOCATIONS_PARAM = "location";
    private static final String PARAMETER_PARAM = "parameter";
    private static final String DATE_FROM_PARAM = "date_from";
    private static final String DATE_TO_PARAM = "date_to";
    private static final String PAGE_PARAM = "page";
    private static final String COUNT_PARAM = "limit";

    public String getLocationsUrl(int page, int count) {
        return getEndpointFullUrlBuilder(endpoints.getLocations())
                .queryParam(PAGE_PARAM, page)
                .queryParam(COUNT_PARAM, count)
                .toUriString();
    }

    private UriComponentsBuilder getEndpointFullUrlBuilder(String endpoint) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(endpoint);
    }

    public String getMeasurementsUrl(Long location, String parameter, OffsetDateTime dateFrom, OffsetDateTime dateTo, int page, int count) {
        return getMeasurementsUrl(Collections.singleton(location), parameter, dateFrom, dateTo, page, count);
    }

    public String getMeasurementsUrl(@NonNull Collection<Long> locationIds, String parameter, OffsetDateTime dateFrom, OffsetDateTime dateTo, int page, int count) {
        UriComponentsBuilder builder = getEndpointFullUrlBuilder(endpoints.getMeasurements())
                .queryParamIfPresent(DATE_FROM_PARAM, Optional.ofNullable(dateFrom))
                .queryParamIfPresent(DATE_TO_PARAM, Optional.ofNullable(dateTo))
                .queryParamIfPresent(PARAMETER_PARAM, Optional.ofNullable(parameter))
                .queryParam(PAGE_PARAM, page)
                .queryParam(COUNT_PARAM, count);
        if (locationIds.size() > 0) {
            builder.queryParam(LOCATIONS_PARAM, locationIds);
        }
        return builder.toUriString();
    }

    public String getParametersUrl(int page, int count) {
        return getEndpointFullUrlBuilder(endpoints.getLocations())
                .queryParam(PAGE_PARAM, page)
                .queryParam(COUNT_PARAM, count)
                .toUriString();
    }
}
