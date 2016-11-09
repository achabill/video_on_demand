package vod.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the sort property parameter of the query string is not sortable.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotSortableParameterException extends RuntimeException {
    public NotSortableParameterException(String value) {
        super("value: [" + value + "] is not a sortable parameter on movies.");
    }
}