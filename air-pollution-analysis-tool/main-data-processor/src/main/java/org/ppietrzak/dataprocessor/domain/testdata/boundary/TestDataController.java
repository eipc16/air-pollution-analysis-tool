package org.ppietrzak.dataprocessor.domain.testdata.boundary;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.services.CalculationRequestService;
import org.ppietrzak.dataprocessor.domain.orders.logic.OrderService;
import org.ppietrzak.dataprocessor.domain.orders.model.Order;
import org.ppietrzak.dataprocessor.domain.testdata.logic.TestDataService;
import org.ppietrzak.dataprocessor.domain.testdata.model.TestData;
import org.ppietrzak.dataprocessor.domain.testdata.repository.mongo.TestDataMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/testData")
@Controller
public class TestDataController {

    private static final int BATCH_SIZE = 1000;

    private final CalculationRequestService calculationRequestService;
    private final OrderService orderService;
    private final TestDataService testDataService;

    @RequestMapping(value = "/{calculationRequestId}", method = RequestMethod.GET, produces= MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(@PathVariable("calculationRequestId") String calculationRequestId, HttpServletResponse response) {

        CalculationRequest calculationRequest = calculationRequestService.getById(calculationRequestId);
        Order order = orderService.getOrderById(calculationRequest.getOrderId());
        String fileName = String.format("%s-%s-%s_test.csv", order.getName(), order.getStartDate().toString(), order.getEndDate().toString())
                .replace("\\s+", "_").replace("\\s", "_");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + fileName + "\""
        );

        List<String> headers = Arrays.asList(
                "Year", "Month", "Day", "Hour", "Minute",
                "Latitude", "Longitude",
                "Satellite Measurement", "Ground Measurement", "Prediction");

        String headerString = String.join(";", headers) + "\n";

        try {
            OutputStream os = response.getOutputStream();
            Page<TestData> testDataPage = null;
            int currentPage = 0;

            os.write(headerString.getBytes(StandardCharsets.UTF_8));

            do {
                testDataPage = testDataService.getTestDataByOrderId(calculationRequestId, currentPage, BATCH_SIZE);
                currentPage++;

                StringBuilder stringBuilder = new StringBuilder();

                for (TestData testData : testDataPage) {
                    stringBuilder.append(String.format("%d;%d;%d;%d;%d;%s;%s;%s;%s;%s\n",
                            testData.getYear(), testData.getMonth(), testData.getDay(),
                            testData.getHour(), testData.getMinute(),
                            testData.getLatitude(), testData.getLongitude(),
                            testData.getSatelliteMeasurement(), testData.getTrueMeasurement(), testData.getPredictions()));
                }

                os.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));

            } while (!testDataPage.isLast());

            os.flush();
            os.close();;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
