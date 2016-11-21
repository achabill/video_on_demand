package vod.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Movie;

import java.util.List;

/**
 * Custom interface for MongoRepository
 */
public interface MoviesRepository extends MongoRepository<Movie, String> {
  List<Movie> findByTitle(String title);

  List<Movie> findByGenre(String genre);

  List<Movie> findByReleaseyear(int releaseyear);

  Movie findById(String id);
}
