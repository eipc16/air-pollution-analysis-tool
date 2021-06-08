package org.ppietrzak.grounddatacore.domain.measurements.boundary;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsPageDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsRequestBodyDTO;
import org.ppietrzak.grounddatacore.client.openaq.OpenAQClient;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQMeasurementDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQMeasurementPaginatedResultDTO;
import org.ppietrzak.grounddatacore.client.openaq.dto.OpenAQPaginatedResultMetaDTO;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class MeasurementsResource {

    private final OpenAQClient openAQClient;

    @PostMapping
    @ResponseBody
    public MeasurementsPageDTO getMeasurements(@RequestBody MeasurementsRequestBodyDTO measurementsRequestBodyDTO,
                                               @RequestParam("page") int page,
                                               @RequestParam("size") int count) {
        OpenAQMeasurementPaginatedResultDTO openAQPage = openAQClient.getMeasurements(
                measurementsRequestBodyDTO.getStations(),
                measurementsRequestBodyDTO.getParameter(),
                measurementsRequestBodyDTO.getDateFromAsOffsetDateTime(),
                measurementsRequestBodyDTO.getDateToAsOffsetDateTime(),
                page,
                count
        );
        OpenAQPaginatedResultMetaDTO meta = openAQPage.getMeta();
        return MeasurementsPageDTO.builder()
                .totalCount(meta.getFound())
                .totalPages((int) Math.ceil(meta.getFound() / (count * 1.0)))
                .currentPage(meta.getPage() > 0 ? meta.getPage() - 1 : 0)
                .count(openAQPage.getContent().size())
                .measurements(openAQPage.getContent().stream()
                        .map(this::mapToMeasurementDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private MeasurementDTO mapToMeasurementDTO(OpenAQMeasurementDTO openAQMeasurementDTO) {
        return MeasurementDTO.builder()
                .locationId(openAQMeasurementDTO.getLocationId())
                .locationName(openAQMeasurementDTO.getLocationName())
                .parameter(openAQMeasurementDTO.getParameter())
                .value(openAQMeasurementDTO.getValue())
                .longitude(openAQMeasurementDTO.getCoordinates().getLongitude())
                .latitude(openAQMeasurementDTO.getCoordinates().getLatitude())
                .measurementDate(openAQMeasurementDTO.getOpenAQDate().getUtc())
                .build();
    }
}
