package br.com.jobson.infra;

import br.com.jobson.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    private ResponseEntity<DefaultResponseError> userAlreadyRegisteredHandler(UserAlreadyRegisteredException exception) {
        DefaultResponseError defaultResponse = new DefaultResponseError(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(defaultResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<DefaultResponseError> userNotFoundHandler(UserNotFoundException exception) {
        DefaultResponseError defaultResponse = new DefaultResponseError(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultResponse);
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    private ResponseEntity<DefaultResponseError> invalidLoginCredentialsHandler(InvalidLoginCredentialsException exception) {
        DefaultResponseError defaultResponse = new DefaultResponseError(HttpStatus.FORBIDDEN, exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(defaultResponse);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    private ResponseEntity<DefaultResponseError> passwordMismatchHandler(PasswordMismatchException exception) {
        DefaultResponseError defaultResponse = new DefaultResponseError(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(defaultResponse);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    private ResponseEntity<DefaultResponseError> sessionNotFoundHandler(SessionNotFoundException exception) {
        DefaultResponseError defaultResponse = new DefaultResponseError(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultResponse);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    private ResponseEntity<DefaultResponseError> unauthorizedActionHandler(UnauthorizedActionException exception) {
        DefaultResponseError defaultResponse = new DefaultResponseError(HttpStatus.UNAUTHORIZED, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(defaultResponse);
    }

    private Map<String, String> mapError(FieldError fieldError) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("field", fieldError.getField());
        mapping.put("message", fieldError.getDefaultMessage());

        return mapping;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");

        List<Map<String, String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapError)
                .toList();
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }


}
