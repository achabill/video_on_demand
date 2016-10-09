package vod.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.User;

import java.util.List;

public interface UsersRepository extends MongoRepository<User,String>
{
    public List<User> findByUsername(String username);
    public User findById(String id);
}