package vod.Repositories;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.Comment;
import vod.models.Movie;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comment,String>
{
    Comment findById(String id);
    List<Comment> findByMovieid(String movieid, Pageable pageable);
    List<Comment> findBySeriesid(String seiresid, Pageable pageable);
    List<Comment> findByMusicid(String musicid, Pageable pageable);
}
