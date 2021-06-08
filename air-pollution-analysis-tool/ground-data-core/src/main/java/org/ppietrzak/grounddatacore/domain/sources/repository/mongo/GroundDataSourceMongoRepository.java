package org.ppietrzak.grounddatacore.domain.sources.repository.mongo;

import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroundDataSourceMongoRepository extends MongoRepository<GroundDataSource, String> {

    Optional<GroundDataSource> findGroundDataSourceByName(String name);
}
