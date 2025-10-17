package br.com.jobson.exceptions;

public class InvalidLoginCredentialsException extends RuntimeException {
    public InvalidLoginCredentialsException() {
        super("Invalid Email or Password");
    }
    public InvalidLoginCredentialsException(String message) {
        super(message);
    }
}
