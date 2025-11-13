package tech.waterfall.register.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tech.waterfall.register.dto.Task;



public class InvalidTaskException extends ResponseStatusException {

    public InvalidTaskException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public InvalidTaskException(Task task, String reason) {
        super(HttpStatus.BAD_REQUEST, String.join(",", reason, task.toString()));
    }

}
