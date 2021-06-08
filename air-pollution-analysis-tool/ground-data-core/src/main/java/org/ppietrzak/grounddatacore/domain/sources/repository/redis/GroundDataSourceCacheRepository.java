package org.ppietrzak.grounddatacore.domain.sources.repository.redis;

import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroundDataSourceCacheRepository extends PagingAndSortingRepository<GroundDataSource, String> {

    Optional<GroundDataSource> findGroundDataSourceByName(String name);
}
