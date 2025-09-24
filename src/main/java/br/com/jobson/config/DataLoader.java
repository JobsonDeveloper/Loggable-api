package br.com.jobson.config;

import br.com.jobson.domain.Role;
import br.com.jobson.domain.User;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final IRoleRepository iRoleRepository;
    private final IUserRepository iUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataLoader(IRoleRepository iRoleRepository, IUserRepository iUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.iRoleRepository = iRoleRepository;
        this.iUserRepository = iUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role roleAdmin = iRoleRepository.findByName(Role.Values.ADMIN.name());
        List<Role> roles = iRoleRepository.findAll();
        var userAdmin = iUserRepository.findByEmail("admin@admin.com");

        // Estava tratando o erro de Role duplicada e detached Role ----

        if (roleAdmin == null) {
            roleAdmin = new Role();
            roleAdmin.setName(Role.Values.ADMIN.name());
            roleAdmin = iRoleRepository.save(roleAdmin);
        }

        if (userAdmin.isEmpty()) {
            var user = new User();
            user.setFirstName("admin");
            user.setLastName("jobs");
            user.setEmail("admin@admin.com");
            user.setPassword(passwordEncoder.encode("123"));
            user.setRoles(Set.of(roleAdmin));
            iUserRepository.save(user);
            System.out.println("Admin criado com sucesso");

        }
    }
}
