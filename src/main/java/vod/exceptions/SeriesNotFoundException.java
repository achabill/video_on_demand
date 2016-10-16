package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.BAD_REQUEST)
public class SeriesNotFoundException extends RuntimeException{
    public SeriesNotFoundException(String id){
        super("The series with id = " + id + " does not exists.");
    }
}
