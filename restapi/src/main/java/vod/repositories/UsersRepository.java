package vod.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.User;

public interface UsersRepository extends MongoRepository<User, String> {
  public User findByUsername(String username);

  public User findById(String id);
}
