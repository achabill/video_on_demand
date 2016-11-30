package vod.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.SeasonEpisode;

import java.util.List;

/**
 * IEpisodeDAO
 *
 * @author Acha Bill [achabill12[at]gmail[dot]com]
 */
public interface IEpisodeDao extends MongoRepository<SeasonEpisode, String> {

  /**
   * Gets a list of episodes by season id
   * @param seasonid The season id
   * @return The list of episodes
   */
  public List<SeasonEpisode> findBySeasonid(String seasonid);

  /**
   * Gest a season episode by id
   * @param id The season episode id
   * @return the season episode
   */
  public SeasonEpisode findById(String id);
}
