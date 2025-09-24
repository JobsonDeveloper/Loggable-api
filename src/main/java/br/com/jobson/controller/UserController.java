package br.com.jobson.controller;

import br.com.jobson.controller.dto.user.CreateUserDto;
import br.com.jobson.domain.Role;
import br.com.jobson.domain.User;
import br.com.jobson.exceptions.PasswordMismatchException;
import br.com.jobson.exceptions.UserAlreadyRegisteredException;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.IUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@Tag(name = "User", description = "User-related operations")
public class UserController {

    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(IUserRepository iUserRepository, IRoleRepository iRoleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.iUserRepository = iUserRepository;
        this.iRoleRepository = iRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostMapping("/user/register")
    @Operation(
            summary = "Register user",
            description = "Register a new user.",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "firstname",
                            description = "User first name",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "lastname",
                            description = "User last name",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    ),
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
                    @Parameter(
                            name = "passwordConfirmation",
                            description = "User confirmation password",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<Map<String, UUID>> register(@Valid @RequestBody CreateUserDto dto)
            throws PasswordMismatchException, UserAlreadyRegisteredException {
        var basicRole = iRoleRepository.findByName(Role.Values.BASIC.name()); // Pegar o tipos de Roles básica
        var userFromDB = iUserRepository.findByEmail(dto.email()); // Verificar se o usuário já existe
        var samePassword = dto.password().equals(dto.confirmPassword()); // Compara a senha e a confirmação de senha envada

        if (userFromDB.isPresent()) {
            throw new UserAlreadyRegisteredException("User already registered!");
        } else if (!samePassword) {
            throw new PasswordMismatchException("The password and password confirmation must be the same!");
        }

        var user = new User(); // Cria o usuário

        user.setFirstName(dto.firstName()); // Adiciona o nome do usuário
        user.setLastName(dto.lastName()); // Adiciona o sobrenome do usuário
        user.setEmail(dto.email()); // Adiciona o email do usuário
        user.setPassword(passwordEncoder.encode(dto.password())); // Adiciona a senha já encriptada
        user.setRoles(Set.of(basicRole)); // Adiciona a Role ao usuário

        var savedUser = iUserRepository.save(user); // Salva o usuário

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", savedUser.getUserId()));
    }

    @Transactional
    @GetMapping("/user/getUsers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(
            summary = "List the users",
            description = "List all users in the system",
            tags = {"User"}
    )
    public ResponseEntity<List<User>> getUsers() {
        var users = iUserRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
