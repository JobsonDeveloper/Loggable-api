package br.com.jobson;

import br.com.jobson.domain.Role;
import br.com.jobson.domain.Session;
import br.com.jobson.domain.User;
import br.com.jobson.repository.IRoleRepository;
import br.com.jobson.repository.ISessionRepository;
import br.com.jobson.repository.IUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
public class AuthenticationTests {
    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private ISessionRepository iSessionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.application.zone}")
    private String appZone;

    @Test
    @Transactional
    public void registerTest() {
        User user = new User();
        Role basicRole = iRoleRepository.findByName(Role.Values.BASIC.name());

        user.setFirstName("User");
        user.setLastName("Test");
        user.setEmail("jobson@gmail.com");
        user.setRoles(Set.of(basicRole));
        user.setPassword(passwordEncoder.encode("@Test2025"));
        User savedUser = iUserRepository.save(user);

        Assertions.assertNotNull(savedUser);
        Assertions.assertTrue(savedUser.getEmail().equals("jobson@gmail.com"));
    }

    @Test
    @Transactional
    public void saveNewSessionTest() {
        Session session = new Session();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(appZone));
        UUID randomId = UUID.randomUUID();

        session.setCreatedAt(now);
        session.setIpAddress("0.0.0.0");
        session.setLoginOn(now);
        session.setUserAgent("UserAgentTest");
        session.setUserDevice("UserDeviceTest");
        session.setUserId(randomId);
        Session savedSession = iSessionRepository.save(session);

        Assertions.assertNotNull(savedSession);
        Assertions.assertTrue(savedSession.getUserId().equals(randomId));
    }
}
