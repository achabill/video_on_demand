package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyLoggedInException extends RuntimeException {
  public AlreadyLoggedInException(String message) {
    super(message);
  }
}
