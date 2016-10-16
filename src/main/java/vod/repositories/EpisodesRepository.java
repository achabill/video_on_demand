package vod.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import vod.models.SeasonEpisode;

import java.util.List;

public interface EpisodesRepository extends MongoRepository<SeasonEpisode,String> {
    public List<SeasonEpisode> findBySeasonid(String id);
    public SeasonEpisode findById(String id);
}
