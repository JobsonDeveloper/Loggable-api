package br.com.jobson.exceptions;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException() {
        super("User already registered!");
    }

    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}
