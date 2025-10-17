package br.com.jobson.infra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DefaultResponseError {
    private HttpStatus status;
    private String message;

    public DefaultResponseError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
