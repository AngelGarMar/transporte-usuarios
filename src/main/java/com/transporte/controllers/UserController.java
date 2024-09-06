package com.transporte.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transporte.constants.Constants;
import com.transporte.dto.LoginUserRequest;
import com.transporte.dto.RegisterUserRequest;
import com.transporte.models.ResponseService;
import com.transporte.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseService> register(@RequestBody @Valid RegisterUserRequest registerUserRequest, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        ResponseService registeredUser = userService.registerOneUser(registerUserRequest);
        return new ResponseEntity<ResponseService>(registeredUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "login", description = "Autenticación del usuario.", tags = {"transporte-usuarios-4"})
    @Parameter(name = "loginUserRequest", description = "Objeto con el usuario y contraseña del usuario.", example = "username='angel', password='angel'", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta de usuario generada correctamente.")
    })
    //https://www.kranio.io/blog/integra-swagger-con-spring-boot-documenta-tus-apis-restful
    public ResponseEntity<ResponseService> login(@RequestBody @Valid LoginUserRequest loginUserRequest, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        ResponseService loggedUser = userService.login(loginUserRequest);
        return new ResponseEntity<ResponseService>(loggedUser, HttpStatus.OK);
    }
    

    private ResponseEntity<ResponseService> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return new ResponseEntity<ResponseService>(new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.FALTAN_CAMPOS, errors), HttpStatus.OK);
    }
}
