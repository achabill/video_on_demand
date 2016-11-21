package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the requested genre is not valid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGenreParameterException extends RuntimeException {
  public InvalidGenreParameterException(String value) {
    super("value: [" + value + "] as genre order does not exists.");
  }
}
