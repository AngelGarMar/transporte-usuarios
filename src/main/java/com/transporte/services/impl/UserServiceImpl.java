package com.transporte.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transporte.constants.Constants;
import com.transporte.dto.LoginUserRequest;
import com.transporte.dto.RegisterUserRequest;
import com.transporte.dto.RegisteredUserResponse;
import com.transporte.entities.Role;
import com.transporte.entities.Token;
import com.transporte.entities.User;
import com.transporte.models.ResponseService;
import com.transporte.repositories.RoleRepository;
import com.transporte.repositories.TokenRepository;
import com.transporte.repositories.UserRepository;
import com.transporte.services.JwtService;
import com.transporte.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    @Transactional(readOnly = false)
    public ResponseService registerOneUser(RegisterUserRequest registerUserRequest) {
        if (!registerUserRequest.getPassword().equals(registerUserRequest.getRepeatedPassword())) {
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.PASSWORD_NO_IGUALES, null);
        }
        //Verifica si existe el usuario
        Optional<User> userOp = userRepository.findByUsername(registerUserRequest.getUsername());
        if (userOp.isPresent()) {
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USUARIO_EXISTENTE, null);
        }
        Optional<Role> roleOp = roleRepository.findByNombre(registerUserRequest.getRole());
        if (roleOp.isEmpty()) {
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.ROLE_NO_EXISTENTE, null);
        }
        Role roleDb = roleOp.get();
        User userSaved = User.builder()
            .nombre(registerUserRequest.getName())
            .apaterno(registerUserRequest.getApaterno())
            .amaterno(registerUserRequest.getAmaterno())
            .username(registerUserRequest.getUsername())
            .status(1)
            .roles(new ArrayList<>())
            .creado(new Date())
            .password(passwordEncoder.encode(registerUserRequest.getPassword()))
            .build();
        userSaved.addRole(roleDb);
        userSaved = userRepository.save(userSaved);
        RegisteredUserResponse userResponse = new RegisteredUserResponse(userSaved, "jwt");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USUARIO_GUARDADO, userResponse);
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseService login(@Valid LoginUserRequest loginUserRequest) {
        log.info("buscar usuario: {}", loginUserRequest.getUsername());
        Optional<User> userOp = userRepository.findByUsername(loginUserRequest.getUsername());
        if (userOp.isEmpty()) {
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USUARIO_NO_ENCONTRADO, null);
        }
        User userDb = userOp.get();
        if (!passwordEncoder.matches(loginUserRequest.getPassword(), userDb.getPassword())) {
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.PASSWORD_INCORRECTO, null);
        }
        if (userDb.getStatus() == 0) {
            return new ResponseService(Constants.RESPONSE_TYPE_WARNING, Constants.USUARIO_INACTIVO, null);
        }
        String jwt = jwtService.generateToken(userOp.get(), generateExtraClaims(userDb));
        RegisteredUserResponse userResponse = new RegisteredUserResponse(userDb, jwt);
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USUARIO_CORRECTO, userResponse);
    }

    public Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getNombre() + " " + user.getApaterno());
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> {
            roles.add("ROLE_" + role.getNombre());
        });
        extraClaims.put("roles", roles);
        extraClaims.put("authorities", user.getAuthorities());
        return extraClaims;
    }

    private void saveUserToken(User user, String jwt) {
        Token token = new Token();
        token.setToken(jwt);
        token.setUser(user);
        token.setExpiration(jwtService.extractExpiration(jwt));
        token.setValid(true);
        tokenRepository.save(token);
    }
}
