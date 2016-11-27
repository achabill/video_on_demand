package vod.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Movie;

import java.util.List;

/**
 * IMovieDAO
 *
 * @author Acha Bill [achabill12[at]gmail[dot]com]
 */
public interface IMovieDao extends MongoRepository<Movie, String> {

  /**
   * Gets a list of movies matching the title.
   * @param title The search title
   * @return The list of movies
   */
  List<Movie> findByTitle(String title);

  /**
   * Gets a list of movies with the genre
   * @param genre The genre
   * @return The list of movies
   */
  List<Movie> findByGenre(String genre);

  /**
   * Gets a list of movies with the release year
   * @param releaseyear The release year
   * @return The list of movies
   */
  List<Movie> findByReleaseyear(int releaseyear);

  /**
   * Gets a movie by id
   * @param id The id of the movie
   * @return The movie
   */
  Movie findById(String id);
}
