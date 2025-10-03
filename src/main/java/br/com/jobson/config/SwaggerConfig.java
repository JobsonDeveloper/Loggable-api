package br.com.jobson.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loggable API")
                        .version("1.0.0")
                        .description("API para manipulação autenticada de usuários")
                        .contact(new Contact()
                                .name("Jobson Oliveira")
                                .email("jobsondeveloper@gmail.com")
                                .url("https://jobsondeveloper.vercel.app/")));
    }

}
