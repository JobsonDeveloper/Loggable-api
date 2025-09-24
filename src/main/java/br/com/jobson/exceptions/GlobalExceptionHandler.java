package br.com.jobson.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<Map<String, String>> handlePasswordMismatch(PasswordMismatchException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("message", exc.getMessage())
        );
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("message", exc.getMessage())
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException exc) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("message", exc.getMessage())
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("message", exc.getMessage())
        );
    }
}
