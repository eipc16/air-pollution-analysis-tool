package org.ppietrzak.dataprocessor.domain.satellitesources.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteDataCoreClient;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteSourceDTO;
import org.ppietrzak.dataprocessor.client.satellitedatacore.SatelliteSourceParameterDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SatelliteSourcesQueryResolver implements GraphQLQueryResolver {

    private final SatelliteDataCoreClient satelliteDataCoreClient;

    public List<SatelliteSourceDTO> getSatelliteSources() {
        return satelliteDataCoreClient.getSources();
    }

    public List<SatelliteSourceParameterDTO> getSatelliteParameters(String source) {
        return satelliteDataCoreClient.getSourceParameters(source);
    }
}
