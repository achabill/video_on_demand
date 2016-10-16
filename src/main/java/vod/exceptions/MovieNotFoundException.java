package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the requested movie with id is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String id) {
        super("Could not find movie with id: " + id + ".");
    }
}