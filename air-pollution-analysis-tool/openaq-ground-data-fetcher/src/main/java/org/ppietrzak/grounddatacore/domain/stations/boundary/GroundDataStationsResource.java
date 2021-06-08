package org.ppietrzak.grounddatacore.domain.stations.boundary;

import lombok.AllArgsConstructor;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceIndexationStatusDTO;
import org.ppietrzak.grounddatacore.domain.stations.logic.GroundStationsIndexationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/reindex")
public class GroundDataStationsResource {

    private final GroundStationsIndexationService groundStationsIndexationService;

    @PostMapping
    public GroundDataSourceIndexationStatusDTO reindexStations() {
        return groundStationsIndexationService.indexAllStations();
    }

    @GetMapping("/status")
    public GroundDataSourceIndexationStatusDTO getStatus() {
        return groundStationsIndexationService.getIndexationStatus();
    }
}
