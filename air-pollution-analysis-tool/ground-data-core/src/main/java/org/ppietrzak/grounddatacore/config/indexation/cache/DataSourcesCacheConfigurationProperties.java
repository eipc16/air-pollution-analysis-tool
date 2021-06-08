package org.ppietrzak.grounddatacore.config.indexation.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "configuration.cache.sources")
@Slf4j
@Getter
@Setter
public class DataSourcesCacheConfigurationProperties {

    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    private long initialDelay;
    private long interval;
    private TimeUnit unit;

    public void setTimeUnit(String unit) {
        try {
            this.unit = TimeUnit.valueOf(unit);
        } catch (IllegalArgumentException ex) {
            log.warn("TimeUnit not found: {}. Using default: {}", unit, DEFAULT_TIME_UNIT);
        }
    }
}
