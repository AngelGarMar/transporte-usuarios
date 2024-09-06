package com.transporte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transporte.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndStatus(String username, int status);
}
