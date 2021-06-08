package org.ppietrzak.dataprocessor.config.services;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.text.MessageFormat;

@ConfigurationProperties(prefix = "configuration.services")
@Setter
public class ServicesConfigurationProperties {

    private ServiceConfiguration groundDataCore;

    private ServiceConfiguration satelliteDataCore;

    private ServiceConfiguration calculationServiceCore;

    public ServiceConfiguration getServiceConfiguration(Services service) {
        switch (service) {
            case GROUND_DATA_CORE:
                return groundDataCore;
            case SATELLITE_DATA_CORE:
                return satelliteDataCore;
            case CALCULATION_SERVICE_CORE:
                return calculationServiceCore;
            default:
                throw new RuntimeException(MessageFormat.format("Couldn't find service with name: {0}", service.getServiceName()));
        }
    }
}
