package com.transporte.dto;

import java.io.Serializable;

import com.transporte.entities.User;

import lombok.Data;

@Data
public class RegisteredUserResponse implements Serializable {
    private Long id;
    private String username;
    private String name;
    private String apaterno;
    private String jwt;

    public RegisteredUserResponse(User user, String jwt) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getNombre();
        this.apaterno = user.getApaterno();
        this.jwt = jwt;
    }
}
