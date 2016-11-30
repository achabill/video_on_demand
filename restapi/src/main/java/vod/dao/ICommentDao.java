package vod.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Comment;
import java.util.List;

/**
 * Comment DAO
 *
 * @author Acha Bill [achabill12[at]gmail[dot]com]
 */
public interface ICommentDao extends MongoRepository<Comment, String> {

  /**
   * Gets the comment with the id
   * @param id The id of the comment
   * @return The comment with the specified id
   */
  Comment findById(String id);

  /**
   * Gets a list of comments to a movie
   * @param movieid The id of the movie
   * @return A list of comments
   */
  List<Comment> findByMovieid(String movieid);

  /**
   * Gets a list of comments to a series
   * @param seriesid The series id
   * @return The list of comments
   */
  List<Comment> findBySeriesid(String seriesid);

  /**
   * Gets a list of comments for a season
   * @param seasonid The season id
   * @return The list of comments
   */
  List<Comment> findBySeasonid(String seasonid);

  /**
   * Gets a list of comments for a season episode
   * @param seasonepisodeid The season episode id
   * @return The list of comments
   */
  List<Comment> findBySeasonepisodeid(String seasonepisodeid);
}
