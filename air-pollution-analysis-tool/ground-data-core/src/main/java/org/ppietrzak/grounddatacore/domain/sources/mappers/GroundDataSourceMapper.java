package org.ppietrzak.grounddatacore.domain.sources.mappers;

import org.ppietrzak.grounddatacore.api.parameters.GroundDataParameterDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataParameter;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ApplicationScope
public class GroundDataSourceMapper {

    public GroundDataSource toEntity(GroundDataSourceDTO groundDataSourceDTO) {
        return GroundDataSource.builder()
                .name(groundDataSourceDTO.getName())
                .id(groundDataSourceDTO.getName())
                .fullName(groundDataSourceDTO.getFullName())
                .dataUrl(groundDataSourceDTO.getDataUrl())
                .reindexUrl(groundDataSourceDTO.getReindexUrl())
                .reindexStatusUrl(groundDataSourceDTO.getReindexStatusUrl())
                .heartBeatUrl(groundDataSourceDTO.getHeartBeatUrl())
                .parameters(groundDataSourceDTO.getParameters().stream()
                        .map(this::toEntity)
                        .collect(Collectors.toSet()))
                .minDate(groundDataSourceDTO.getMinDate())
                .maxDate(groundDataSourceDTO.getMaxDate())
                .build();
    }

    public GroundDataParameter toEntity(GroundDataParameterDTO groundDataParameterDTO) {
        return new GroundDataParameter(groundDataParameterDTO.getName());
    }

    public List<GroundDataSourceDTO> toDTO(Collection<GroundDataSource> groundDataSources) {
        return groundDataSources.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GroundDataSourceDTO toDTO(GroundDataSource groundDataSource) {
        return GroundDataSourceDTO.builder()
                .id(groundDataSource.getId())
                .name(groundDataSource.getName())
                .fullName(groundDataSource.getFullName())
                .dataUrl(groundDataSource.getDataUrl())
                .reindexUrl(groundDataSource.getReindexUrl())
                .lastUpdated(groundDataSource.getLastUpdated())
                .reindexStatusUrl(groundDataSource.getReindexStatusUrl())
                .heartBeatUrl(groundDataSource.getHeartBeatUrl())
                .parameters(groundDataSource.getParameters().stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList()))
                .minDate(groundDataSource.getMinDate())
                .maxDate(groundDataSource.getMaxDate())
                .build();
    }

    public List<GroundDataParameterDTO> toDTOParameter(Collection<GroundDataParameter> groundDataParameters) {
        return groundDataParameters.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GroundDataParameterDTO toDTO(GroundDataParameter groundDataParameter) {
        return GroundDataParameterDTO.create(groundDataParameter.getName());
    }
}
