package org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.logic.SatelliteMeasurementsService;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.model.SatelliteMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/satelliteMeasurements")
@Controller
public class SatelliteMeasurementsController {

    private static final int BATCH_SIZE = 10000;

    private final OrderService orderService;
    private final SatelliteMeasurementsService satelliteMeasurementsService;

    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(@PathVariable("orderId") String orderId, HttpServletResponse response) {
        Order order = orderService.getOrderById(orderId);
        String fileName = String.format("%s-%s-%s_satellite.csv", order.getName(), order.getStartDate().toString(), order.getEndDate().toString())
                .replace("\\s+", "_").replace("\\s", "_");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + fileName + "\""
        );

        List<String> headers = Arrays.asList(
                "Year", "Month", "Day", "Hour", "Minute",
                "Latitude", "Longitude", "Value");

        String headerString = String.join(";", headers) + "\n";

        try {
            OutputStream os = response.getOutputStream();
            Page<SatelliteMeasurement> measurementPage = null;
            int currentPage = 0;

            os.write(headerString.getBytes(StandardCharsets.UTF_8));

            do {
                measurementPage = satelliteMeasurementsService.findAllByOrderId(orderId, currentPage, BATCH_SIZE);
                currentPage++;

                StringBuilder stringBuilder = new StringBuilder();

                for (SatelliteMeasurement measurement : measurementPage) {
                    stringBuilder.append(String.format("%d;%d;%d;%d;%d;%s;%s;%s\n",
                            measurement.getYear(), measurement.getMonth(), measurement.getDay(),
                            measurement.getHour(), measurement.getMinute(),
                            measurement.getLatitude(), measurement.getLongitude(),
                            measurement.getValue()));
                }

                os.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));

            } while (!measurementPage.isLast());

            os.flush();
            os.close();
            ;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}