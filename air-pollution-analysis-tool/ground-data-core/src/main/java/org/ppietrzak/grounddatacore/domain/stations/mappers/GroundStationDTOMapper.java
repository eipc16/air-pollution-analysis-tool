package org.ppietrzak.grounddatacore.domain.stations.mappers;

import org.ppietrzak.grounddatacore.api.stations.GroundDataStationDTO;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
@Service
public class GroundStationDTOMapper {

    public GroundStation mapToEntity(String source, GroundDataStationDTO groundDataStationDTO) {
        return GroundStation.builder()
                .id(groundDataStationDTO.getId())
                .source(source)
                .name(groundDataStationDTO.getName())
                .latitude(groundDataStationDTO.getLatitude())
                .longitude(groundDataStationDTO.getLongitude())
                .parameters(groundDataStationDTO.getParameters())
                .build();
    }

    public GroundDataStationDTO toDTO(GroundStation groundStation) {
        return GroundDataStationDTO.builder()
                .id(groundStation.getId())
                .name(groundStation.getName())
                .latitude(groundStation.getLatitude())
                .longitude(groundStation.getLongitude())
                .parameters(groundStation.getParameters())
                .build();
    }
}
