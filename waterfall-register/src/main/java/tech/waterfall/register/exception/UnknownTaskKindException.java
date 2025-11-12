package tech.waterfall.register.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnknownTaskKindException extends ResponseStatusException {
    private static final String MESSAGE_PREFIX = "Unknown task kind : ";

    public UnknownTaskKindException(String taskName) {
        super(HttpStatus.BAD_REQUEST, MESSAGE_PREFIX + taskName);
    }
}
