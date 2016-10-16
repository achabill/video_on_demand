package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import vod.models.Series;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeriesSeasonNotFoundException extends RuntimeException {
        public SeriesSeasonNotFoundException(String id){
            super("The season with id = " + id + " does not exists.");
        }
}

