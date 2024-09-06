package com.transporte.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.transporte.security.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig { //se agregó para seguridad
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityFilterChain filterChain = http
            .cors(Customizer.withDefaults())
            .csrf(csrfConfig -> csrfConfig.disable())
            .sessionManagement(sessMagConfig -> sessMagConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authReqConfig -> {
                authReqConfig.requestMatchers(HttpMethod.POST, "/users/register").hasRole("administrador");
                authReqConfig.requestMatchers(HttpMethod.POST, "/users/login").permitAll();
                authReqConfig.anyRequest().authenticated();
            })
            .exceptionHandling(exceptionConfig -> {
                exceptionConfig.authenticationEntryPoint(authenticationEntryPoint); //para la clase CustomAuthenticationEntryPoint, cuando no viene el token
                exceptionConfig.accessDeniedHandler(accessDeniedHandler); //para la clase CustomAccessDeniedHandler, cuando no se tiene el rol para la url solicitada
            })
            .build();
        return filterChain;
    }
}
