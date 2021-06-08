package org.ppietrzak.grounddatacore.config.source;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@ConfigurationProperties(prefix = "configuration.source")
@Setter
public class SourceConfigurationProperties {

    @Getter
    private String name;

    @Getter
    private String fullName;

    private String scheme;
    private String host;
    private Long port;
    private SourceEndpointConfiguration endpoints;

    public String getDataUrl() {
        return getEndpointFullUrl(endpoints.getData());
    }

    private String getEndpointFullUrl(String endpoint) {
        return UriComponentsBuilder.newInstance()
//                .scheme(Optional.ofNullable(scheme).orElse("http"))
                .host(host)
                .port(Optional.ofNullable(port).map(String::valueOf).orElse(null))
                .path(endpoint)
                .toUriString();
    }

    public String getHeartBeatUrl() {
        return getEndpointFullUrl(endpoints.getHeartBeat());
    }

    public String getReindexUrl() {
        return getEndpointFullUrl(endpoints.getReindex());
    }

    public String getReindexStatusUrl() {
        return getEndpointFullUrl(endpoints.getReindexStatus());
    }
}