package com.transporte.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserRequest implements Serializable {
    @Size(min = 4, max = 15)
    @NotBlank
    private String username;

    @Size(min = 4, max = 10)
    @NotBlank
    private String password;
}
