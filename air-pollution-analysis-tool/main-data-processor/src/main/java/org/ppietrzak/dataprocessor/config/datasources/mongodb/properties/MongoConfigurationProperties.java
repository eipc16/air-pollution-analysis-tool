package org.ppietrzak.dataprocessor.config.datasources.mongodb.properties;

import lombok.Getter;
import lombok.Setter;
import org.ppietrzak.dataprocessor.config.shared.Credentials;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties(prefix = "configuration.mongo")
@Getter
@Setter
public class MongoConfigurationProperties {

    private String databaseName;
    private boolean autoCreateIndices = false;
    private String authSource;
    private String host;
    private String port;
    private Credentials credentials;

    public Optional<String> getAuthSource() {
        return Optional.ofNullable(authSource);
    }
}
