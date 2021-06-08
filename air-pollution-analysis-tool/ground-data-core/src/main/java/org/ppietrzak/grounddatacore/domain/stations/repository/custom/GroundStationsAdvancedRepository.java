package org.ppietrzak.grounddatacore.domain.stations.repository.custom;

import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.stations.logic.CoordinatesBox;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroundStationsAdvancedRepository {

    Page<GroundStation> findAllByStationAndCoordsAndParameter(String source, String parameter, CoordinatesBox coordinatesBox, Pageable pageable);

    Page<GroundStation> findAllBySourceAndCoords(String source, CoordinatesBox coordinatesBox, Pageable pageable);

    List<GroundStation> findAllByStationAndCoordsAndParameter(String source, String parameter, CoordinatesBox coordinatesBox);

    Page<GroundStation> findAllBySourceAndParameter(String source, String parameter, CoordinatesBox coordinatesBox, Pageable pageable);

    List<GroundStation> getAllStationsBySource(GroundDataSource groundDataSource);
}
