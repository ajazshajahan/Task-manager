package com.example.task_manager.service.impl;

import com.example.task_manager.service.JwtService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    public JwtServiceImpl(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }


    @Override
    public String generateToken(String username) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600)) // Token expiration time (e.g., 1 hour)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public boolean validateToken(String token) {

        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            Instant now = Instant.now();

            // Check if the token has expired
            return Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(now);
        } catch (JwtException e) {
            // Invalid token
            return false;
        }
    }
}
