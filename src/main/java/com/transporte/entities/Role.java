package com.transporte.entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cat_roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 25)
    @Column(name = "nombre")
    private String nombre;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnoreProperties({"roles", "handler", "hibernateLazyInitializer"})
    private List<User> usuarios;

    @ManyToMany
    @JoinTable(name = "roles_operaciones", joinColumns = @JoinColumn(name = "roles_id"),
        inverseJoinColumns = @JoinColumn(name = "operaciones_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"roles_id", "operaciones_id"})})
    @JsonIgnoreProperties({"roles", "handler", "hibernateLazyInitializer"})
    private List<Operation> operaciones;
}
