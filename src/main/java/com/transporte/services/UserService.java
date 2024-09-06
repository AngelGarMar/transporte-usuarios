package com.transporte.services;

import com.transporte.dto.LoginUserRequest;
import com.transporte.dto.RegisterUserRequest;
import com.transporte.models.ResponseService;

import jakarta.validation.Valid;

public interface UserService {

    ResponseService registerOneUser(RegisterUserRequest registerUserRequest);

    ResponseService login(@Valid LoginUserRequest loginUserRequest);

}
