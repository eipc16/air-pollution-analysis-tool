package org.ppietrzak.dataprocessor.domain.calculationrequests.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.services.CalculationRequestService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculationRequestQueryResolver implements GraphQLQueryResolver {

    private final CalculationRequestService calculationRequestService;

    public List<CalculationRequest> getCalculationRequests(String orderId) {
        return calculationRequestService.findByOrderId(orderId);
    }
}
