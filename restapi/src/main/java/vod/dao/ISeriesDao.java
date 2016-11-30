package vod.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Series;

import java.util.List;

/**
 * SeriesDAO
 *
 * @author Acha Bill [achabill12[at]gmail[dot]com]
 */
public interface ISeriesDao extends MongoRepository<Series, String> {

  /**
   * Gets a list of series matching the title
   * @param title The title
   * @return The list of movies
   */
  public List<Series> findByTitle(String title);

  /**
   * Gest a series by id
   * @param id The id
   * @return The series
   */
  public Series findById(String id);

  /**
   * Gets a list of series by release year
   * @param releaseyear The release year
   * @return The list of series
   */
  public List<Series> findByReleaseyear(int releaseyear);

  /**
   * Gets a list of series by genre
   * @param genre The genre
   * @return The list of series
   */
  public List<Series> findByGenre(String genre);
}
