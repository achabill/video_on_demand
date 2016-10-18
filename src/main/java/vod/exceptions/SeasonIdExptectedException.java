package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeasonIdExptectedException extends RuntimeException {
    public SeasonIdExptectedException() {
        super("Expected season id.");
    }
}
