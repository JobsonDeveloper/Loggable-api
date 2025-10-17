package br.com.jobson.unit;

import br.com.jobson.util.TokenGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.UUID;

@SpringBootTest
public class TokenTests {
    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Test
    public void tokenGeneratorTest() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        String token = tokenConstructor(sessionId, userId);

        Assertions.assertNotNull(token);
    }

    @Test
    public void tokenDecodeTest() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        String token = tokenConstructor(sessionId, userId);

        Assertions.assertNotNull(token);

        Jwt tokenInfo = jwtDecoder.decode(token);
        String tokenClaim = tokenInfo.getClaim("sessionId");

        Assertions.assertNotNull(tokenClaim);
        Assertions.assertTrue(tokenClaim.equals(sessionId.toString()));
    }

    public String tokenConstructor(UUID sessionId, UUID userId) {
        Long expiresIn = 300L;
        String scopes = "BASIC";

        String jwtValue = tokenGenerator.generateToken(
                userId,
                sessionId,
                scopes,
                expiresIn
        );

        return jwtValue;
    }
}