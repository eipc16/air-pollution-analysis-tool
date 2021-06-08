package org.ppietrzak.grounddatacore.domain.stations.repository.mongo;

import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.ppietrzak.grounddatacore.domain.stations.repository.custom.GroundStationsAdvancedRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroundStationsMongoRepository extends MongoRepository<GroundStation, String>, GroundStationsAdvancedRepository {

}
