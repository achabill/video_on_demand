package vod.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Movie;

import java.util.List;

/**
 * Custom interface for MongoRepository
 */
public interface MoviesRepository extends MongoRepository<Movie, String> {
    List<Movie> findByTitle(String title);

    List<Movie> findByGenre(String genre, Pageable pageable);

    List<Movie> findByReleaseyear(int releaseyear, Pageable pageable);

    Movie findById(String id);
}
