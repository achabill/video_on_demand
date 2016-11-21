package vod.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Comment;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comment, String> {
  Comment findById(String id);

  List<Comment> findByMovieid(String movieid);

  List<Comment> findBySeriesid(String seriesid);

  List<Comment> findBySeasonid(String seasonid);

  List<Comment> findByMusicid(String musicid);

  List<Comment> findBySeasonepisodeid(String seasonepisodeid);
}
