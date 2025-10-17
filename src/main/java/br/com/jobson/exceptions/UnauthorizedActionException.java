package br.com.jobson.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException() {
        super("You don't have permission to do this!");
    }

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
