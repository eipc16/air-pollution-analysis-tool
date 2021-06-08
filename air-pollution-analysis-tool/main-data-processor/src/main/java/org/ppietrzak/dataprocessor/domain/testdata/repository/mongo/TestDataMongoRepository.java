package org.ppietrzak.dataprocessor.domain.testdata.repository.mongo;

import org.ppietrzak.dataprocessor.domain.testdata.model.TestData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDataMongoRepository extends MongoRepository<TestData, String> {

    Page<TestData> findAllByCalculationRequestId(String calculationRequestId, Pageable pageable);

    void deleteAllByCalculationRequestId(String calculationRequestId);
}
