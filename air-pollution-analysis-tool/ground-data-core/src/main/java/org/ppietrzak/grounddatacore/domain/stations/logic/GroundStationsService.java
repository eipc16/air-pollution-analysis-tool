package org.ppietrzak.grounddatacore.domain.stations.logic;

import lombok.AllArgsConstructor;
import org.ppietrzak.grounddatacore.api.parameters.GroundDataParameterDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceIndexationStatusDTO;
import org.ppietrzak.grounddatacore.client.sources.SourceFetcherClient;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.ppietrzak.grounddatacore.domain.stations.repository.mongo.GroundStationsMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroundStationsService {

    private final GroundStationsMongoRepository mongoRepository;
    private final SourceFetcherClient sourceFetcherClient;

    public List<GroundStation> saveStations(Collection<GroundStation> groundStation) {
        return mongoRepository.saveAll(groundStation);
    }

    public Page<GroundStation> findAll(Pageable pageable) {
        return mongoRepository.findAll(pageable);
    }

    public List<GroundStation> findByCoordsAndParameter(String source, String parameter, CoordinatesBox coordinatesBox) {
        return mongoRepository.findAllByStationAndCoordsAndParameter(source, parameter, coordinatesBox);
    }

    public Page<GroundStation> findByCoords(String source, @Nullable String parameter, CoordinatesBox coordinatesBox, Pageable pageable) {
        if (parameter == null) {
            return mongoRepository.findAllBySourceAndCoords(source, coordinatesBox, pageable);
        }
        return mongoRepository.findAllByStationAndCoordsAndParameter(source, parameter, coordinatesBox, pageable);
    }

    public GroundDataSourceIndexationStatusDTO indexStationsFromSource(GroundDataSource groundDataSource) {
        return indexStationsFromSources(Collections.singletonList(groundDataSource)).values().stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Couldn't index source: {0}", groundDataSource.getFullName())));
    }

    public Map<String, GroundDataSourceIndexationStatusDTO> indexStationsFromSources(List<GroundDataSource> groundDataSources) {
        return groundDataSources.stream()
                .collect(Collectors.toMap(
                        GroundDataSource::getName,
                        sourceFetcherClient::reindexStations
                ));
    }

    public GroundDataSourceIndexationStatusDTO getIndexationStatus(GroundDataSource groundDataSource) {
        return getIndexationStatuses(Collections.singletonList(groundDataSource)).values().stream()
                .findAny()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Couldn't fetch source: {0}", groundDataSource.getFullName())));
    }

    public Map<String, Optional<GroundDataSourceIndexationStatusDTO>> getIndexationStatuses(List<GroundDataSource> groundDataSources) {
        return groundDataSources.stream()
                .collect(Collectors.toMap(
                        GroundDataSource::getName,
                        sourceFetcherClient::getIndexationStatus
                ));
    }

    public Page<GroundStation> findAllBySource(String source, @Nullable String parameter, CoordinatesBox coordinatesBox, Pageable pageable) {
        if (parameter == null) {
            return mongoRepository.findAllBySourceAndCoords(source, coordinatesBox, pageable);
        }
        return mongoRepository.findAllBySourceAndParameter(source, parameter, coordinatesBox, pageable);
    }

    public List<GroundDataParameterDTO> getAllParametersBySource(GroundDataSource groundDataSource) {
        List<GroundStation> stationsForSource = mongoRepository.getAllStationsBySource(groundDataSource);
        Set<String> parameters = new HashSet<>();
        stationsForSource.forEach(source -> parameters.addAll(source.getParameters()));
        return parameters.stream()
                .map(GroundDataParameterDTO::create)
                .collect(Collectors.toList());
    }
}
