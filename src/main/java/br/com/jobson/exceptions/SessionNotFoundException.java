package br.com.jobson.exceptions;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("Session not found!");
    }

    public SessionNotFoundException(String message) {
        super(message);
    }
}
