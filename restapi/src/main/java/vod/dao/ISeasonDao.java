package vod.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Season;

import java.util.List;

/**
 * ISeasonDAO
 *
 * @author Acha Bill [achabill12[at]gmail[dot]com]
 */
public interface ISeasonDao extends MongoRepository<Season, String> {

  /**
   * Gets a list of seasons by series id
   * @param seriesid The series id
   * @return The list of seasons
   */
  public List<Season> findBySeriesid(String seriesid);

  /**
   * Gets a season by id
   * @param id The id
   * @return The season
   */
  public Season findById(String id);
}
