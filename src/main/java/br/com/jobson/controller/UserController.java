package br.com.jobson.controller;

import br.com.jobson.controller.dto.swagger.ReturnMessageResponseSwaggerDto;
import br.com.jobson.controller.dto.swagger.ReturnUserResponseSwaggerDto;
import br.com.jobson.controller.dto.user.GetUserResponseDto;
import br.com.jobson.controller.dto.user.MessageResponseDto;
import br.com.jobson.domain.Role;
import br.com.jobson.domain.Session;
import br.com.jobson.domain.User;
import br.com.jobson.exceptions.SessionNotFoundException;
import br.com.jobson.exceptions.UnauthorizedActionException;
import br.com.jobson.exceptions.UserNotFoundException;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.ISessionRepository;
import br.com.jobson.repository.IUserRepository;
import br.com.jobson.util.TokenGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ua_parser.Parser;

import java.security.interfaces.RSAPublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@Tag(name = "User", description = "User-related operations")
public class UserController {

    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final JwtDecoder jwtDecoder;
    private final ISessionRepository iSessionRepository;
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;
    @Value("${spring.application.zone}")
    private String appZone;
    Parser uaParcer = new Parser();

    public UserController(IUserRepository iUserRepository, IRoleRepository iRoleRepository, BCryptPasswordEncoder passwordEncoder, TokenGenerator tokenGenerator, JwtDecoder jwtDecoder, ISessionRepository iSessionRepository) {
        this.iUserRepository = iUserRepository;
        this.iRoleRepository = iRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.jwtDecoder = jwtDecoder;
        this.iSessionRepository = iSessionRepository;
    }

    @Operation(
            summary = "Get a user",
            description = "Returns a user's information",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnUserResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error verifying token data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    )
            }
    )
    @GetMapping("/api/user/info")
    public ResponseEntity<GetUserResponseDto> getUser(@RequestHeader("Authorization") String authorization) {
        String tokenValue;
        Jwt token;
        String subject;

        tokenValue = authorization.replace("Bearer ", "").trim();
        token = jwtDecoder.decode(tokenValue);
        subject = token.getSubject();

        UUID userId = UUID.fromString(subject);
        User user = iUserRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException()
        );

        return ResponseEntity.status(HttpStatus.OK).body(new GetUserResponseDto(
                user.getId(),
                user.getFirstName(), user.getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).findFirst()
        ));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user from the system",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully deleted",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnUserResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "When the client does not have permission to delete the user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnMessageResponseSwaggerDto.class)
                            )
                    )
            }
    )
    @Transactional
    @DeleteMapping("/api/user/delete/{id}")
    public ResponseEntity<MessageResponseDto> deleteUser(@RequestHeader("Authorization") String authorization, @PathVariable UUID id) {
        String tokenValue = authorization.replace("Bearer", "").trim();
        Jwt token = jwtDecoder.decode(tokenValue);
        String userId = token.getSubject();
        String sessionIdClaim = token.getClaim("sessionId");

        if (!userId.equals(id.toString())) {
            throw new UnauthorizedActionException();
        }

        UUID sessionId = UUID.fromString(sessionIdClaim);
        Optional<Session> sessionInfo = iSessionRepository.findById(sessionId);

        if(sessionInfo.isEmpty()) {
            throw new SessionNotFoundException();
        }

        ZonedDateTime logoutTimestamp = ZonedDateTime.now(ZoneId.of(appZone));
        sessionInfo.get().setLogoutOn(logoutTimestamp);
        iSessionRepository.save(sessionInfo.get());

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());
        user.getRoles().clear();
        iUserRepository.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto("User successfully deleted!"));
    }
}
