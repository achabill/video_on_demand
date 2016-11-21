package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the sort order parameter of the query string is not valid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SortOrderParameterException extends RuntimeException {
  public SortOrderParameterException(String value) {
    super("value: [" + value + "] as sorting order is not recognized. Either [asc or desc]");
  }
}

