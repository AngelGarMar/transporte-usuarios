package com.transporte.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.transporte.exception.ObjectNotFoundException;
import com.transporte.repositories.UserRepository;

@Configuration
public class SecurityBeansInjector {
    @Autowired
    //private AuthenticationProvider daoAuthProvider;
    private AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private UserRepository userRepository;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> {
            return userRepository.findByUsernameAndStatus(username, 1).orElseThrow(() -> new ObjectNotFoundException("User not found with username " + username));
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationStrategy = new DaoAuthenticationProvider();
        authenticationStrategy.setPasswordEncoder(this.passwordEncoder());
        authenticationStrategy.setUserDetailsService(userDetailsService());
        return authenticationStrategy;
    }
}
