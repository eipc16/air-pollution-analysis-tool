package org.ppietrzak.dataprocessor.domain.testdata.boundary;

import graphql.kickstart.tools.GraphQLResolver;
import org.ppietrzak.dataprocessor.domain.testdata.model.TestData;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class TestDataDateResolver implements GraphQLResolver<TestData> {

    public OffsetDateTime date(TestData testData) {
        return OffsetDateTime.of(
                (int) testData.getYear(),
                (int) testData.getMonth(),
                (int) testData.getDay(),
                (int) testData.getHour(),
                (int) testData.getMinute(),
                0,
                0,
                ZoneOffset.UTC
        );
    }
}