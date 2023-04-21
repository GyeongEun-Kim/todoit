package app.todoit.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.todoit.domain.challenge.entity.Challenger;

@Repository
public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
}