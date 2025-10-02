package br.com.jobson.repository;

import br.com.jobson.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISessionRepository extends JpaRepository<Session, UUID> {
}
