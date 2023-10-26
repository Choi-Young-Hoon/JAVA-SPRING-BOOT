package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtTokenProvider;
import org.example.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JwtTokenService {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!this.jwtTokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = this.refreshTokenService.findByRefreshToken(refreshToken).getId();
        User user = this.userService.findById(userId);

        return this.jwtTokenProvider.generateToken(user, this.getExpiredDuration());
    }

    // 내 인증 정보에서 id 값을 가져온다.
    public static Optional<String> getCurrentUsername() {

        // authentication객체가 저장되는 시점은 JwtFilter의 doFilter 메소드에서
        // Request가 들어올 때 SecurityContext에 Authentication 객체를 저장해서 사용
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }

    public Duration getExpiredDuration() {
        return Duration.ofHours(2);
    }
}
