package com.transporte.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cat_operaciones")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "nombre")
    private String nombre;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "path")
    private String path;

    @NotBlank
    @Size(min = 1, max = 25)
    @Column(name = "http_method")
    private String httpMethod;

    @NotNull
    @Column(name = "permit_all")
    private boolean permitAll;

    @ManyToMany(mappedBy = "operaciones")
    @JsonIgnoreProperties({"operaciones", "handler", "hibernateLazyInitializer"})
    private List<Role> roles;

    @ManyToOne
    @JoinColumn(name = "modulos_id")
    private Module module;
}
