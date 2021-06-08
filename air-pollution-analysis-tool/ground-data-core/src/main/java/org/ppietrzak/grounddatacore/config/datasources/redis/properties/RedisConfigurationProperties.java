package org.ppietrzak.grounddatacore.config.datasources.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.ppietrzak.grounddatacore.config.shared.Credentials;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "configuration.redis")
@Getter
@Setter
public class RedisConfigurationProperties {

    private String host;
    private Integer port;
    private Credentials credentials;
}
