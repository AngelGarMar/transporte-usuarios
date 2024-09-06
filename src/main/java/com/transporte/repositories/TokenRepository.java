package com.transporte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transporte.entities.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}
