package org.ppietrzak.dataprocessor.domain.testdata.logic;

import lombok.RequiredArgsConstructor;
import org.ppietrzak.dataprocessor.domain.testdata.model.TestData;
import org.ppietrzak.dataprocessor.domain.testdata.repository.mongo.TestDataMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestDataService {

    private final TestDataMongoRepository testDataRepository;

    public Page<TestData> getTestDataByOrderId(String calculationOrderId, int page, int size) {
        return testDataRepository.findAllByCalculationRequestId(calculationOrderId, PageRequest.of(page, size));
    }
}
