package br.com.jobson.integration;

import br.com.jobson.domain.Role;
import br.com.jobson.domain.User;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.IUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class UserTests {
    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void userInfoTest() {
        User user = userConstructor();
        User savedUser = iUserRepository.save(user);

        Assertions.assertNotNull(savedUser);

        iUserRepository.delete(savedUser);
        Optional<User> deletedUser = iUserRepository.findById(savedUser.getId());

        Assertions.assertTrue(deletedUser.isEmpty());
    }

    @Transactional
    public User userConstructor() {
        Role basicRole = iRoleRepository.findByName(Role.Values.BASIC.name());

        User user = new User();
        user.setFirstName("User");
        user.setLastName("Test");
        user.setEmail("jobson@gmail.com");
        user.setRoles(Set.of(basicRole));
        user.setPassword(passwordEncoder.encode("@Test2025"));

        return user;
    }
}
