package com.transporte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transporte.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombre(String nombre);
}
