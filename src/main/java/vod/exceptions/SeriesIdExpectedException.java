package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeriesIdExpectedException extends  RuntimeException {
    public SeriesIdExpectedException(){
        super("Expected series id.");
    }
}
