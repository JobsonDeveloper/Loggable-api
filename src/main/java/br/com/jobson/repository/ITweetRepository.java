package br.com.jobson.repository;

import br.com.jobson.domain.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITweetRepository extends JpaRepository<Tweet, Long> {

}
