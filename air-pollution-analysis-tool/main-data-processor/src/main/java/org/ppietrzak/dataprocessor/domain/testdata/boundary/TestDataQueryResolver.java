package org.ppietrzak.dataprocessor.domain.testdata.boundary;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.testdata.logic.TestDataService;
import org.ppietrzak.dataprocessor.domain.testdata.model.TestData;
import org.ppietrzak.dataprocessor.infrastructure.graphql.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataQueryResolver implements GraphQLQueryResolver {

    private final TestDataService testDataService;

    public Pagination<TestData> getTestData(String calculationRequestId, int page, int size) {
        Page<TestData> testData = testDataService.getTestDataByOrderId(calculationRequestId, page, size);
        return Pagination.from(testData);
    }
}
