package com.example.server.gameRequests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRequestRepository extends JpaRepository<GameRequest, String> {}

