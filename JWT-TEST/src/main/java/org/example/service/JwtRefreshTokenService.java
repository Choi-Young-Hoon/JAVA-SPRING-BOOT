package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.JwtRefreshToken;
import org.example.repository.JwtRefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtRefreshTokenService {
    private final JwtRefreshTokenRepository refreshTokenRepository;

    public JwtRefreshToken findByRefreshToken(String refreshToken) {
        return this.refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
