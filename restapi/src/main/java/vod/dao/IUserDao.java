package vod.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.User;

/**
 * IUserDAO
 *
 * @author Acha Bill [achabill12[at]gmail[dot]com]
 */
public interface IUserDao extends MongoRepository<User, String> {

  /**
   * Gets a user by username
   * @param username The username
   * @return The user
   */
  public User findByUsername(String username);

  /**
   * Ges a user by id
   * @param id The id
   * @return The user
   */
  public User findById(String id);
}
