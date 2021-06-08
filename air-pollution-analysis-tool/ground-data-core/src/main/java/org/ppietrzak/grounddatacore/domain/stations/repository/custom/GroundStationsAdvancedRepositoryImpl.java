package org.ppietrzak.grounddatacore.domain.stations.repository.custom;

import lombok.AllArgsConstructor;
import org.ppietrzak.grounddatacore.domain.sources.model.GroundDataSource;
import org.ppietrzak.grounddatacore.domain.stations.logic.CoordinatesBox;
import org.ppietrzak.grounddatacore.domain.stations.model.GroundStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;

import java.util.List;

@AllArgsConstructor
public class GroundStationsAdvancedRepositoryImpl implements GroundStationsAdvancedRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<GroundStation> findAllByStationAndCoordsAndParameter(String source, String parameter, CoordinatesBox coordinatesBox, Pageable pageable) {
        Query query = buildCoordsQuery(source, coordinatesBox, pageable)
                .addCriteria(Criteria.where("parameters").in(parameter));
        List<GroundStation> result = mongoTemplate.find(query, GroundStation.class);
        return PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), GroundStation.class)
        );
    }

    private Query buildCoordsQuery(String source, CoordinatesBox coordinatesBox, @Nullable Pageable pageable) {
        return buildBySourceQuery(source, pageable)
                .addCriteria(Criteria.where("latitude")
                        .gt(coordinatesBox.getBottomLatitude())
                        .lt(coordinatesBox.getUpperLatitude()))
                .addCriteria(Criteria.where("longitude")
                        .gt(coordinatesBox.getBottomLongitude())
                        .lt(coordinatesBox.getUpperLongitude()));
    }

    @Override
    public Page<GroundStation> findAllBySourceAndCoords(String source, CoordinatesBox coordinatesBox, Pageable pageable) {
        Query query = buildCoordsQuery(source, coordinatesBox, pageable);
        List<GroundStation> result = mongoTemplate.find(query, GroundStation.class);
        return PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), GroundStation.class)
        );
    }

    @Override
    public List<GroundStation> findAllByStationAndCoordsAndParameter(String source, String parameter, CoordinatesBox coordinatesBox) {
        Query query = buildCoordsQuery(source, coordinatesBox, null)
                .addCriteria(Criteria.where("parameters").in(parameter));
        return mongoTemplate.find(query, GroundStation.class);
    }

    @Override
    public Page<GroundStation> findAllBySourceAndParameter(String source, String parameter, CoordinatesBox coordinatesBox, Pageable pageable) {
        Query query = buildCoordsQuery(source, coordinatesBox, pageable)
                .addCriteria(Criteria.where("parameters").in(parameter));
        List<GroundStation> result = mongoTemplate.find(query, GroundStation.class);
        return PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), GroundStation.class)
        );
    }

    private Query buildBySourceQuery(String source, @Nullable Pageable pageable) {
        Query query = new Query();
        if(pageable != null) {
            query = query.with(pageable);
        }
        return query.addCriteria(Criteria.where("source").is(source));
    }

    @Override
    public List<GroundStation> getAllStationsBySource(GroundDataSource groundDataSource) {
        Query query = new Query()
                .addCriteria(Criteria.where("source").is(groundDataSource.getName()));
        List<GroundStation> result = mongoTemplate.find(query, GroundStation.class);
        return result;
    }
}