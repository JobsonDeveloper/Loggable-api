package br.com.jobson.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("admin");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    private String token;

    private String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate testTemplate;

    HttpHeaders headers = new HttpHeaders();

    public UserTests() {
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void GetInfo() {
        ResponseEntity<Map> registerResponse = handleRegister("getInfo@test.com");
        assertThat(registerResponse.getBody()).isNotNull();

        String token = (String) registerResponse.getBody().get("accessToken");
        ResponseEntity<Map> response = handleGetUserInfo(token);
        assertThat(response.getBody()).isNotNull();

        Object userEmail = response.getBody().get("email");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userEmail).isEqualTo("getInfo@test.com");
    }

    @Test
    public void Delete() {
        ResponseEntity<Map> registerResponse = handleRegister("delete@test.com");
        assertThat(registerResponse.getBody()).isNotNull();

        String token = (String) registerResponse.getBody().get("accessToken");
        ResponseEntity<Map> getInfoResponse = handleGetUserInfo(token);
        assertThat(getInfoResponse.getBody()).isNotNull();

        String userId = (String) getInfoResponse.getBody().get("id");

        this.headers.setBearerAuth(this.token);
        String url = this.baseUrl + this.port + "/api/user/delete/" + userId;

        HttpEntity<String> request = new HttpEntity<>(this.headers);
        ResponseEntity<String> response = this.testTemplate.exchange(
                url,
                HttpMethod.DELETE,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public ResponseEntity<Map> handleRegister(String email) {
        String url = this.baseUrl + this.port + "/api/auth/register";
        String body = """
        {
            "firstName" : "username",
            "lastName" : "testando",
            "email" : "%s",
            "password" : "!UserTest4444",
            "confirmPassword" : "!UserTest4444"
        }""".formatted(email);

        HttpEntity<String> request = new HttpEntity<>(body, this.headers);
        ResponseEntity<Map> response = this.testTemplate.postForEntity(url, request, Map.class);
        this.token = (String) response.getBody().get("accessToken");

        return response;
    }

    public ResponseEntity<Map> handleGetUserInfo(String token) {
        this.headers.setBearerAuth(token);
        String url = this.baseUrl + this.port + "/api/user/info";

        HttpEntity<Void> request = new HttpEntity<>(this.headers);
        ResponseEntity<Map> response = this.testTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        return response;
    }
}
