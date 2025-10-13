package br.com.jobson.controller;

import br.com.jobson.controller.dto.swagger.TokenResponseSwaggerDto;
import br.com.jobson.controller.dto.swagger.ReturnMessageResponseSwaggerDto;
import br.com.jobson.controller.dto.user.CreateUserDto;
import br.com.jobson.controller.dto.user.LoginRegisterResponseDto;
import br.com.jobson.controller.dto.user.LoginRequestDto;
import br.com.jobson.domain.Role;
import br.com.jobson.domain.Session;
import br.com.jobson.domain.User;
import br.com.jobson.exceptions.InvalidCredentialsException;
import br.com.jobson.exceptions.PasswordMismatchException;
import br.com.jobson.exceptions.UserAlreadyRegisteredException;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.ISessionRepository;
import br.com.jobson.repository.IUserRepository;
import br.com.jobson.util.TokenGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import ua_parser.Client;
import ua_parser.Parser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Authentication", description = "User-related authentications")
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final TokenGenerator tokenGenerator;
    private final IRoleRepository iRoleRepository;
    private final IUserRepository iUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ISessionRepository iSessionRepository;
    @Value("${spring.application.zone}")
    private String appZone;
    Parser uaParcer = new Parser();

    public TokenController(
            JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, TokenGenerator tokenGenerator, IRoleRepository iRoleRepository,
            IUserRepository iUserRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder, ISessionRepository iSessionRepository
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenGenerator = tokenGenerator;
        this.iRoleRepository = iRoleRepository;
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.iSessionRepository = iSessionRepository;
    }

    @Operation(
            summary = "User login",
            description = "Logs in a user",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid email or password",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    )
            }
    )
    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginRegisterResponseDto> login(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request) throws InvalidCredentialsException {
        Optional<User> user = iUserRepository.findByEmail(loginRequest.email()); // Usuário que contem o 'email' informado
        Boolean passwordEncoded = user.get().isLoginCorrect(loginRequest, passwordEncoder); // Verifica se a senha está correta

        if (user.isEmpty() || !passwordEncoded) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UUID userId = user.get().getId();
        String userAgent = request.getHeader("User-Agent");
        ZonedDateTime loginTimestamp = ZonedDateTime.now(ZoneId.of(appZone));
        Client userDevice = uaParcer.parse(userAgent.toString());
        String userIp = request.getHeader("X-Forwarded-For");
        if (userIp == null || userIp.isBlank()) {
            userIp = request.getRemoteAddr();
        }

        Session session = new Session();
        session.setIpAddress(userIp);
        session.setLoginOn(loginTimestamp);
        session.setUserAgent(userAgent);
        session.setUserDevice(userDevice.os.family);
        session.setUserId(userId);

        Session savedSession = iSessionRepository.save(session);

        Long expiresIn = 86400L; // Tempo de duração de um token (300 = 5 minutos)
        String scopes = user.get().getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.joining(" "));
        String jwtValue = tokenGenerator.generateToken(
                user.get().getId(),
                savedSession.getId(),
                scopes,
                expiresIn
        );

        return ResponseEntity.ok(new LoginRegisterResponseDto(jwtValue, expiresIn));
    }

    @Operation(
            summary = "User logout",
            description = "Performs disconnection of a user's account",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    )
            }
    )
    @Transactional
    @PostMapping("/api/auth/logout")
    public ResponseEntity<List<String>> logout(@RequestHeader("Authorization") String authorization) {
        String tokenValue = authorization.replace("Bearer ", "").trim();
        Jwt token = jwtDecoder.decode(tokenValue);
        String sessionIdClaim = token.getClaim("sessionId");

        try {
            UUID sessionId = UUID.fromString(sessionIdClaim);
            Optional<Session> sessionInfo = iSessionRepository.findById(sessionId);
            ZonedDateTime logoutTimestamp = ZonedDateTime.now(ZoneId.of(appZone));
            sessionInfo.get().setLogoutOn(logoutTimestamp);
            iSessionRepository.save(sessionInfo.get());

            return ResponseEntity.status(HttpStatus.OK).body(List.of("message", "Logout successfully"));
        } catch (Exception e) {
            throw new RuntimeException("Internal server error!");
        }
    }

    @Operation(
            summary = "Register user",
            description = "Register a new user",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "The password and password confirmation must be the same",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already registered",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    )
            }
    )
    @Transactional
    @PostMapping("/api/auth/register")
    public ResponseEntity<LoginRegisterResponseDto> register(@Valid @RequestBody CreateUserDto dto, HttpServletRequest request)
            throws PasswordMismatchException, UserAlreadyRegisteredException, BadRequestException {
        String email = dto.email();
        String password = dto.password();
        String confirmPassword = dto.confirmPassword();

        Role basicRole = iRoleRepository.findByName(Role.Values.BASIC.name()); // Pegar o tipos de Roles básica
        Optional<User> userFromDB = iUserRepository.findByEmail(email); // Verificar se o usuário já existe
        Boolean samePassword = password.equals(confirmPassword); // Compara a senha e a confirmação de senha envada

        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new BadRequestException("Invalid data!");
        }

        if (userFromDB.isPresent()) {
            throw new UserAlreadyRegisteredException("User already registered!");
        } else if (!samePassword) {
            throw new PasswordMismatchException("The password and password confirmation must be the same!");
        }

        User user = new User(); // Cria o usuário

        user.setFirstName(dto.firstName()); // Adiciona o nome do usuário
        user.setLastName(dto.lastName()); // Adiciona o sobrenome do usuário
        user.setEmail(dto.email()); // Adiciona o email do usuário
        user.setPassword(passwordEncoder.encode(dto.password())); // Adiciona a senha já encriptada
        user.setRoles(Set.of(basicRole)); // Adiciona a Role ao usuário

        User savedUser = iUserRepository.save(user); // Salva o usuário
        UUID userId = savedUser.getId();
        String userAgent = request.getHeader("User-Agent");
        ZonedDateTime loginTimestamp = ZonedDateTime.now(ZoneId.of(appZone));
        Client userDevice = uaParcer.parse(userAgent.toString());
        String userIp = request.getHeader("X-Forwarded-For");
        if (userIp == null || userIp.isBlank()) {
            userIp = request.getRemoteAddr();
        }

        Session session = new Session();
        session.setIpAddress(userIp);
        session.setLoginOn(loginTimestamp);
        session.setUserAgent(userAgent);
        session.setUserDevice(userDevice.os.family);
        session.setUserId(userId);

        Session savedSession = iSessionRepository.save(session);

        Instant now = Instant.now();
        Long expiresIn = 86400L;
        String scopes = user.getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.joining(" "));
        String jwtValue = tokenGenerator.generateToken(
                user.getId(),
                savedSession.getId(),
                scopes,
                expiresIn
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginRegisterResponseDto(jwtValue, expiresIn));
    }
}
