package org.ppietrzak.dataprocessor.domain.calculationrequests.boundary;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;
import org.ppietrzak.dataprocessor.domain.calculationrequests.services.CalculationRequestService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalculationRequestMutationResolver implements GraphQLMutationResolver {

    private final CalculationRequestService calculationRequestService;

    public CalculationRequest getCreateCalculationRequest(CalculationRequestBodyDTO calculationRequest) {
        return calculationRequestService.startCalculationRequest(calculationRequest);
    }
}
