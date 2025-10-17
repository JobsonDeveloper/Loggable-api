package br.com.jobson.integration;

import br.com.jobson.domain.Role;
import br.com.jobson.repository.IRoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
class RolesTests {

	@Autowired
	private IRoleRepository iRoleRepository;

	@Test
	@Transactional
	public void createRoleTest() {
		Role role = new Role();
		role.setName("ROLETEST");
		Role savedRole = iRoleRepository.save(role);

		Assertions.assertNotNull(savedRole.getRoleId());
		Assertions.assertTrue(savedRole.getName().equals(role.getName()));
	}

	@Test
	@Transactional
	public void deleteRole() {
		Role role = new Role();
		role.setName("ROLETEST");
		Role savedRole = iRoleRepository.save(role);

		Assertions.assertNotNull(savedRole.getRoleId());
		Assertions.assertTrue(savedRole.getName().equals(role.getName()));

		iRoleRepository.delete(savedRole);
		Optional<Role> deletedRole = iRoleRepository.findById(savedRole.getRoleId());

		Assertions.assertTrue(deletedRole.isEmpty());
	}
}
