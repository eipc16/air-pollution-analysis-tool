package org.ppietrzak.dataprocessor.domain.satellitepredictions.boundary;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.services.CalculationRequestService;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.logic.SatellitePredictionService;
import org.ppietrzak.dataprocessor.domain.satellitepredictions.model.SatellitePrediction;
import org.ppietrzak.dataprocessor.domain.testdata.logic.TestDataService;
import org.ppietrzak.dataprocessor.domain.testdata.model.TestData;
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
@RequestMapping("/satellitePredictions")
@Controller
public class SatellitePredictionController {

    private static final int BATCH_SIZE = 10000;

    private final CalculationRequestService calculationRequestService;
    private final OrderService orderService;
    private final SatellitePredictionService satellitePredictionService;

    @RequestMapping(value = "/{calculationRequestId}", method = RequestMethod.GET, produces= MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(@PathVariable("calculationRequestId") String calculationRequestId, HttpServletResponse response) {

        CalculationRequest calculationRequest = calculationRequestService.getById(calculationRequestId);
        Order order = orderService.getOrderById(calculationRequest.getOrderId());
        String fileName = String.format("%s-%s-%s_result.csv", order.getName(), order.getStartDate().toString(), order.getEndDate().toString())
                .replace("\\s+", "_").replace("\\s", "_");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + fileName + "\""
        );

        List<String> headers = Arrays.asList(
                "Year", "Month", "Day", "Hour", "Minute",
                "Latitude", "Longitude",
                "Satellite Measurement", "Prediction");

        String headerString = String.join(";", headers) + "\n";

        try {
            OutputStream os = response.getOutputStream();
            Page<SatellitePrediction> predictionsPage = null;
            int currentPage = 0;

            os.write(headerString.getBytes(StandardCharsets.UTF_8));

            do {
                predictionsPage = satellitePredictionService.getByCalculationRequestId(calculationRequestId, currentPage, BATCH_SIZE);
                currentPage++;

                StringBuilder stringBuilder = new StringBuilder();

                for (SatellitePrediction prediction : predictionsPage) {
                    stringBuilder.append(String.format("%d;%d;%d;%d;%d;%s;%s;%s;%s\n",
                            prediction.getYear(), prediction.getMonth(), prediction.getDay(),
                            prediction.getHour(), prediction.getMinute(),
                            prediction.getLatitude(), prediction.getLongitude(),
                            prediction.getValue(), prediction.getPredictions()));
                }

                os.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));

            } while (!predictionsPage.isLast());

            os.flush();
            os.close();;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
