package br.com.jobson.controller.dto.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenResponseSwaggerDto")
public class TokenResponseSwaggerDto {
    private String accessToken;
    private Long expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
