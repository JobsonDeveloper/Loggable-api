package br.com.jobson.exceptions;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("The password and password confirmation must be the same!");
    }

    public PasswordMismatchException(String message) {
        super(message);
    }
}
