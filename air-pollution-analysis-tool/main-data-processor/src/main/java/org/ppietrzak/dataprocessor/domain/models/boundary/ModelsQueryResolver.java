package org.ppietrzak.dataprocessor.domain.models.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.client.calculationservice.CalculationServiceCoreClient;
import org.ppietrzak.dataprocessor.client.calculationservice.ModelDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModelsQueryResolver implements GraphQLQueryResolver {

    private final CalculationServiceCoreClient calculationServiceCoreClient;

    public List<ModelDTO> getModels() {
        return calculationServiceCoreClient.getModels();
    }
}
