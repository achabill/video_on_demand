package vod.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Series;

import java.util.List;

/**
 * Series repository interface.
 */
public interface SeriesRepository extends MongoRepository<Series, String> {
    public List<Series> findByTitle(String title);

    public Series findById(String id);

    public Series findByReleaseyear(int releaseyear);

    public List<Series> findByGenre(String genre);
}
