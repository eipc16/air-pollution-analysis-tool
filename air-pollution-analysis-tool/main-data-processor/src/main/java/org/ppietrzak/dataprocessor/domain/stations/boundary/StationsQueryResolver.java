package org.ppietrzak.dataprocessor.domain.stations.boundary;


import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.grounddatacore.GroundDataCoreClient;
import org.ppietrzak.grounddatacore.api.stations.GroundDataStationPageDTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StationsQueryResolver implements GraphQLQueryResolver {

    private final GroundDataCoreClient groundDataCoreClient;

    public GroundDataStationPageDTO getStations(String source, String parameter,
                                                double bottomLatitude, double bottomLongitude,
                                                double upperLatitude, double upperLongitude,
                                                int page, int size) {
        return groundDataCoreClient.getStations(source, parameter, bottomLatitude, bottomLongitude, upperLatitude, upperLongitude, page, size);
    }
}
