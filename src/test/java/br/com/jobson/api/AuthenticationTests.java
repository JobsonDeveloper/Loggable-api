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
import java.util.List;
import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTests {
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

    private String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate testTemplate;

    HttpHeaders headers = new HttpHeaders();

    public AuthenticationTests() {
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void Register() {
        String url = this.baseUrl + this.port + "/api/auth/register";
        String body = """
                {
                    "firstName" : "username",
                    "lastName" : "testando",
                    "email" : "user@test.com",
                    "password" : "!UserTest4444",
                    "confirmPassword" : "!UserTest4444"
                }""";


        HttpEntity<String> request = new HttpEntity<>(body, this.headers);
        ResponseEntity<Map> response = this.testTemplate.postForEntity(url, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        String token = (String) response.getBody().get("accessToken");
        assertThat(token).isNotNull();
    }

    @Test
    public void Login() {
        ResponseEntity<Map> response = handleLogin();
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String token = (String) response.getBody().get("accessToken");
        assertThat(token).isNotNull();
    }

    @Test
    public void Logout() {
        ResponseEntity<Map> loginResponse = handleLogin();
        assertThat(loginResponse.getBody()).isNotNull();
        String token = (String) loginResponse.getBody().get("accessToken");

        String url = baseUrl + port + "/api/auth/logout";

        this.headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(this.headers);
        ResponseEntity<List> response = this.testTemplate.postForEntity(url, request, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public ResponseEntity<Map> handleLogin() {
        String url = this.baseUrl + this.port + "/api/auth/login";
        String body = """
                {
                    "email" : "jobson@admin.com",
                    "password" : "@Admin44"
                }""";

        HttpEntity<String> request = new HttpEntity<>(body, this.headers);
        ResponseEntity<Map> response = this.testTemplate.postForEntity(url, request, Map.class);

        return response;
    }
}
