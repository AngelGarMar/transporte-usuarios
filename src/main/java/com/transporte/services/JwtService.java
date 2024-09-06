package com.transporte.services;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.transporte.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class JwtService {
    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    public String generateToken(User user, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date((EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime()); //en milisegundos
        String jwt = Jwts.builder()
            .header()
                .type("JWT")
            .and()
            .claims(extraClaims)
            .subject(user.getUsername())
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(generateKey(), Jwts.SIG.HS256)
            .compact();
        return jwt;
    }

    private SecretKey generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        System.out.println(new String(passwordDecoded));
        return Keys.hmacShaKeyFor(passwordDecoded);
    }

    public String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization"); //Bearer jwt
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return null; //Retorna a quien mandó llamar este método
        }

        //obtener token jwt desde el encabezado
        String jwt = authorizationHeader.split(" ")[1];
        return jwt;
    }

    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    public List<String> extractRoles(String jwt) {
        return (List<String>) extractAllClaims(jwt).get("roles");
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(jwt).getPayload();
    }

    public Date extractExpiration(String jwt) {
        return extractAllClaims(jwt).getExpiration();
    }
}
