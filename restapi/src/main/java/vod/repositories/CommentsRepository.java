package vod.repositories;


import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Comment;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comment, String> {
    Comment findById(String id);

    List<Comment> findByMovieid(String movieid, Pageable pageable);

    List<Comment> findBySeriesid(String seriesid, Pageable pageable);

    List<Comment> findBySeasonid(String seasonid, Pageable pageable);

    List<Comment> findByMusicid(String musicid, Pageable pageable);

    List<Comment> findBySeasonepisodeid(String seasonepisodeid, Pageable pageable);
}
