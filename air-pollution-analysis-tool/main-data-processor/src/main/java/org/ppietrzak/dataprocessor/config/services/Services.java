package org.ppietrzak.dataprocessor.config.services;

public enum Services {
    GROUND_DATA_CORE("ground-data-core"),
    SATELLITE_DATA_CORE("satellite-data-core"),
    CALCULATION_SERVICE_CORE("calculation-service-core");

    private final String serviceName;

    Services(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}