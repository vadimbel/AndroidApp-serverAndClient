package com.example.server.gameSessions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GameSessionsRepository extends JpaRepository<GameSessions, String> {
    Optional<GameSessions> findByFirstUsernameOrSecondUsername(String firstUsername, String secondUsername);
}

