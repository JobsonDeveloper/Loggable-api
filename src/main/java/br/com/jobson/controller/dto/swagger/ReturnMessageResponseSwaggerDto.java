package br.com.jobson.controller.dto.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReturnMessageResponseSwaggerDto")
public class ReturnMessageResponseSwaggerDto {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
