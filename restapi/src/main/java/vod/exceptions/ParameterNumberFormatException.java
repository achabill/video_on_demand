package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the query string parameter is in wrong number format.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParameterNumberFormatException extends RuntimeException {
  public ParameterNumberFormatException(String parameter, String value) {
    super("value : [" + value + "] is in wrong number format for parameter [" + parameter + "]. Integer required.");
  }
}
