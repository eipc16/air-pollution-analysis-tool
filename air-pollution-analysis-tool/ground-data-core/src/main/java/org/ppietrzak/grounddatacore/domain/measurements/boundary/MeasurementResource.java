package org.ppietrzak.grounddatacore.domain.measurements.boundary;

import lombok.AllArgsConstructor;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsOrderRequestBodyDTO;
import org.ppietrzak.grounddatacore.domain.measurements.logic.MeasurementsService;
import org.ppietrzak.grounddatacore.domain.stations.logic.CoordinatesBox;
import org.ppietrzak.grounddatacore.domain.stations.logic.GroundStationsService;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/measurements")
public class MeasurementResource {

    private final MeasurementsService measurementsService;
    private final GroundStationsService groundStationsService;

    @PostMapping("/fetch")
    @ResponseBody
    public ResponseEntity<?> build(@RequestBody MeasurementsOrderRequestBodyDTO measurementsOrderRequestBodyDTO) {
        List<GroundStation> matchingStations = groundStationsService.findByCoordsAndParameter(
                measurementsOrderRequestBodyDTO.getSource(),
                measurementsOrderRequestBodyDTO.getParameter(),
                new CoordinatesBox(
                        measurementsOrderRequestBodyDTO.getBottomLatitude(),
                        measurementsOrderRequestBodyDTO.getUpperLatitude(),
                        measurementsOrderRequestBodyDTO.getBottomLongitude(),
                        measurementsOrderRequestBodyDTO.getUpperLongitude()
                )
        );
        measurementsService.indexMeasurementsAsynchronously(
                measurementsOrderRequestBodyDTO.getSource(),
                measurementsOrderRequestBodyDTO.getParameter(),
                measurementsOrderRequestBodyDTO.getTargetTopic(),
                measurementsOrderRequestBodyDTO.getOrderId(),
                matchingStations,
                measurementsOrderRequestBodyDTO.getDateFromAsOffsetDateTime(),
                measurementsOrderRequestBodyDTO.getDateToAsOffsetDateTime()
        );
        return ResponseEntity.ok().build();
    }
}
