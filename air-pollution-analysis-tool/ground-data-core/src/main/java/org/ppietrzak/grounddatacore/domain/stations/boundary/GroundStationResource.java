package org.ppietrzak.grounddatacore.domain.stations.boundary;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.grounddatacore.api.stations.GroundDataStationPageDTO;
import org.ppietrzak.grounddatacore.domain.stations.logic.CoordinatesBox;
import org.ppietrzak.grounddatacore.domain.stations.logic.GroundStationsService;
import org.ppietrzak.grounddatacore.domain.stations.mappers.GroundStationDTOMapper;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class GroundStationResource {

    private final GroundStationsService groundStationsService;
    private final GroundStationDTOMapper groundStationDTOMapper;

    @GetMapping("/{source-name}")
    public GroundDataStationPageDTO getStations(@PathVariable("source-name") String sourceName,
                                                @RequestParam(value = "parameter", required = false) String parameter,
                                                @RequestParam("bottomLatitude") Double bottomLatitude,
                                                @RequestParam("bottomLongitude") Double bottomLongitude,
                                                @RequestParam("upperLatitude") Double upperLatitude,
                                                @RequestParam("upperLongitude") Double upperLongitude,
                                                @RequestParam("page") int page,
                                                @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);

        CoordinatesBox coordinatesBox = new CoordinatesBox(
                bottomLatitude,
                upperLatitude,
                bottomLongitude,
                upperLongitude
        );

        Page<GroundStation> foundPage = groundStationsService.findAllBySource(sourceName, parameter, coordinatesBox, pageable);
        return GroundDataStationPageDTO.builder()
                .count(foundPage.getSize())
                .totalCount(foundPage.getTotalElements())
                .page(foundPage.getNumber())
                .totalPages(foundPage.getTotalPages())
                .content(foundPage.getContent().stream()
                        .map(groundStationDTOMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
