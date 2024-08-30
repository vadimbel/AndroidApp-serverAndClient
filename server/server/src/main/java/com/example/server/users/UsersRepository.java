package com.example.server.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// data access layer
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {}
