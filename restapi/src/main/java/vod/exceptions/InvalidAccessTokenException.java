package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class InvalidAccessTokenException extends RuntimeException {
  public InvalidAccessTokenException(String token) {
    super("Invalid access token: " + token);
  }
}
