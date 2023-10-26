package org.example.repository;

import org.example.domain.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, Long> {
    Optional<JwtRefreshToken> findByUserId(Long userId);
    Optional<JwtRefreshToken> findByRefreshToken(String refreshToken);
}
