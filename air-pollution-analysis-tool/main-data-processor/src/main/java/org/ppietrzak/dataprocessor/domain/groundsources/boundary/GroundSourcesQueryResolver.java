package org.ppietrzak.dataprocessor.domain.groundsources.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.grounddatacore.GroundDataCoreClient;
import org.ppietrzak.grounddatacore.api.parameters.GroundDataParameterDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroundSourcesQueryResolver implements GraphQLQueryResolver {

    private final GroundDataCoreClient groundDataCoreClient;

    public List<GroundDataSourceDTO> getGroundSources() {
        return groundDataCoreClient.getSources();
    }

    public List<GroundDataParameterDTO> getGroundParameters(String source) {
        return groundDataCoreClient.getSourceParameters(source);
    }
}
