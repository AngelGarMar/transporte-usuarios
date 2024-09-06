package com.transporte.security.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.transporte.constants.Constants;
import com.transporte.entities.Token;
import com.transporte.models.ResponseService;
import com.transporte.repositories.TokenRepository;
import com.transporte.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Date;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("Entrada al filtro JWT Authentication Filter");
        //obtener encabezado http llamado Authorization
        String jwt = jwtService.extractJwtFromRequest(request);

        //si no viene el token, continua con la ejecucion para las url que no tienen proteccion
        if (jwt == null || !StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        //obtener token no expirado y valido desde base de datos y se valida expiracion
        Optional<Token> token = tokenRepository.findByToken(jwt);
        boolean isValid = validateToken(token);
        if (!isValid) {
            respuesta(response, Constants.RESPONSE_TYPE_ERROR, Constants.NO_TOKEN);
            return;
        }
        boolean isExpirated = expirateToken(token.get());
        if (!isExpirated) {
            respuesta(response, Constants.RESPONSE_TYPE_ERROR, Constants.TOKEN_NO_VALIDO);
            return;
        }

        //obtener el subject/username desde el token, esta accion a su vez valida el token, firma y fecha de expiracion
        String username = jwtService.extractUsername(jwt);
        logger.info("Username: " + username);

        //se obtienen los roles del jwt y se setean dentro de security context holder
        List<String> roles = jwtService.extractRoles(jwt);
        List<SimpleGrantedAuthority> authorityCollection = new ArrayList<>();
        roles.forEach(role -> {
            authorityCollection.add(new SimpleGrantedAuthority(role));
        });
        UsernamePasswordAuthenticationToken authenticationFilter = new UsernamePasswordAuthenticationToken(username, null, authorityCollection);
        SecurityContextHolder.getContext().setAuthentication(authenticationFilter);
        //se imprimen los roles obtenidos del jwt
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(role -> {
            logger.info(role);
        });

        //ejecutar el resto de filtros
        filterChain.doFilter(request, response);
    }

    private boolean validateToken(Optional<Token> optionalToken) {
        if (!optionalToken.isPresent()) {
            logger.info("Token no existe o no fue generado en nuestro sistema");
            return false;
        }
        return true;
    }

    private boolean expirateToken(Token token) {
        Date now = new Date(System.currentTimeMillis());
        boolean isValid = token.isValid() && token.getExpiration().after(now); //aun es valido
        if (!isValid) {
            logger.info("Token invalido");
            updateTokenStatus(token);
        }
        return isValid;
    }

    private void updateTokenStatus(Token token) {
        token.setValid(false);
        tokenRepository.save(token);
    }

    private void respuesta(HttpServletResponse response, String type, String message) throws IOException {
        ResponseService responseService = new ResponseService(type, message, null);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.OK.value());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String apiErrorAsJson = objectMapper.writeValueAsString(responseService);
            response.getWriter().write(apiErrorAsJson);
    }
}
