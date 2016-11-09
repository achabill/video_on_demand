package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentNotProperlyFormattedException extends RuntimeException {
    public CommentNotProperlyFormattedException(String property) {
        super("The property :" + property + " cannot be null");
    }
}
