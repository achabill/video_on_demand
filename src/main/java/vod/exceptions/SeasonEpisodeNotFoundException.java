package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeasonEpisodeNotFoundException extends RuntimeException {
    public SeasonEpisodeNotFoundException(String id) {
        super("The episode with id = " + id + " does does not exist.");
    }
}
