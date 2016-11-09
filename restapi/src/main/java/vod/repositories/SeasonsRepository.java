package vod.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Season;

import java.util.List;

/**
 * The seasons repo.
 */
public interface SeasonsRepository extends MongoRepository<Season, String> {
    public List<Season> findBySeriesid(String seriesid);

    public Season findById(String id);
}
