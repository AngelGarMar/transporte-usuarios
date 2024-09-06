package com.transporte.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.transporte.models.ApiError;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiError apiError = new ApiError();
        apiError.setBackendMessage(accessDeniedException.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage("Acceso denegado. No tienes los permisos necesarios para acceder a esta función. Por favor, contacta con el administrador si crees que esto es un error.");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value()); //403
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String apiErrorAsJson = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(apiErrorAsJson);
    }

}
