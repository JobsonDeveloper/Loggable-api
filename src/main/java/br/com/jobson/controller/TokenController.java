package br.com.jobson.controller;

import br.com.jobson.controller.dto.user.LoginRequestDto;
import br.com.jobson.controller.dto.user.LoginResponseDto;
import br.com.jobson.domain.Role;
import br.com.jobson.exceptions.InvalidCredentialsException;
import br.com.jobson.repository.IUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final IUserRepository iUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenController(
            JwtEncoder jwtEncoder,
            IUserRepository iUserRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.jwtEncoder = jwtEncoder;
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/auth/login")
    @Operation(
            summary = "User login",
            description = "Logs in the user",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "User email",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "password",
                            description = "User password",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    ),
            }
    )
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) throws InvalidCredentialsException {
        var user = iUserRepository.findByEmail(loginRequest.email()); // Usuário que contem o 'email' informado
        var passwordEncoded = user.get().isLoginCorrect(loginRequest, passwordEncoder); // Verifica se a senha está correta

        // Verifica se o usuário existe e se a senha está correta
        if (user.isEmpty() || !passwordEncoded) {
            throw new InvalidCredentialsException("Invalid user or password");
        }

        var now = Instant.now();
        var expiresIn = 1200L; // Tempo de duração de um token (300 = 5 minutos)

        var scopes = user.get().getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.joining(" "));

        // -------- Gerar Token JWT e retornar na requisição
        // Atributos JWT
        var claims = JwtClaimsSet.builder()
                .issuer("bakcend") // Quem está gerando o token (convenção)
                .subject(user.get().getUserId().toString()) // Qual é o usuário
                .issuedAt(now) // Data de criação do token
                .expiresAt(now.plusSeconds(expiresIn)) // Tempo de expiração do token (aogra + 300s)
                .claim("scope", scopes)
                .build();

        // Gerar o Toker a partir dos parametros passados e guardar ele na variável
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Retorna o token e o tempo de expiração para o client
        return ResponseEntity.ok(new LoginResponseDto(jwtValue, expiresIn));
    }
}
