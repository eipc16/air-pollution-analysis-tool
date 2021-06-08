package org.ppietrzak.grounddatacore.domain.sources.boundary;

import lombok.AllArgsConstructor;
import org.ppietrzak.grounddatacore.api.parameters.GroundDataParameterDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceIndexationStatusDTO;
import org.ppietrzak.grounddatacore.domain.sources.logic.GroundDataSourceService;
import org.ppietrzak.grounddatacore.domain.sources.mappers.GroundDataSourceMapper;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.stations.logic.GroundStationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/sources")
public class GroundDataSourceResource {

    private final GroundDataSourceService groundDataSourceService;
    private final GroundStationsService groundStationsService;
    private final GroundDataSourceMapper groundDataSourceMapper;

    @PostMapping("/indexation")
    public Map<String, GroundDataSourceIndexationStatusDTO> indexAll() {
        List<GroundDataSource> dataSources = groundDataSourceService.findAll();
        return groundStationsService.indexStationsFromSources(dataSources);
    }

    @PostMapping("/indexation/{source-name}")
    public GroundDataSourceIndexationStatusDTO indexStation(@PathVariable("source-name") String sourceName) {
        GroundDataSource source = groundDataSourceService.getSourceByName(sourceName);
        return groundStationsService.indexStationsFromSource(source);
    }

    @GetMapping("/indexation/status")
    public Map<String, Optional<GroundDataSourceIndexationStatusDTO>> getIndexationStatus() {
        List<GroundDataSource> dataSources = groundDataSourceService.findAll();
        return groundStationsService.getIndexationStatuses(dataSources);
    }

    @GetMapping("/indexation/{source-name}/status")
    public GroundDataSourceIndexationStatusDTO getIndexationStatus(@PathVariable("source-name") String sourceName) {
        GroundDataSource source = groundDataSourceService.getSourceByName(sourceName);
        return groundStationsService.getIndexationStatus(source);
    }

    @GetMapping
    public List<GroundDataSourceDTO> getAllSources() {
        List<GroundDataSource> dataSources = groundDataSourceService.findAll();
        return groundDataSourceMapper.toDTO(dataSources);
    }

    @GetMapping("/{source-name}/parameters")
    public List<GroundDataParameterDTO> getSourceParameters(@PathVariable("source-name") String sourceName) {
        GroundDataSource source = groundDataSourceService.getSourceByName(sourceName);
        return groundStationsService.getAllParametersBySource(source);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllStations() {
        groundDataSourceService.removeAll();
        return ResponseEntity.ok().build();
    }
}
