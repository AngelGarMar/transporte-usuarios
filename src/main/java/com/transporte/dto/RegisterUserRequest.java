package com.transporte.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest implements Serializable {
    @Size(min = 4, max = 15)
    @NotBlank
    private String name;

    @Size(min = 4, max = 10)
    @NotBlank
    private String apaterno;

    @Size(min = 4, max = 10)
    @NotBlank
    private String amaterno;

    @Size(min = 4, max = 10)
    @NotBlank
    private String username;

    @Size(min = 4, max = 10)
    @NotBlank
    private String password;

    @Size(min = 4, max = 10)
    @NotBlank
    private String repeatedPassword;

    @NotBlank
    private String role;
}
