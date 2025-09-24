package br.com.jobson;

import br.com.jobson.domain.Role;
import br.com.jobson.domain.User;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.IUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class UserRegisterTest {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void register() {
        User user = new User();
        Role basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        user.setFirstName("Jobson");
        user.setLastName("Oliveira");
        user.setEmail("jobson@gmail.com");
        user.setRoles(Set.of(basicRole));
        user.setPassword(passwordEncoder.encode("123"));

        User saveUser = userRepository.save(user);

        Assertions.assertNotNull(saveUser);
    }
}
