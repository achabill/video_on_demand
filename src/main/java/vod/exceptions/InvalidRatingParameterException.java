package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRatingParameterException extends RuntimeException {
    public InvalidRatingParameterException(String value) {
        super("value: [ " + value + " ] for rating is invalid. Rating values include" +
                " [onestar, twostars, threestars, fourstars, fivestars].");
    }
}