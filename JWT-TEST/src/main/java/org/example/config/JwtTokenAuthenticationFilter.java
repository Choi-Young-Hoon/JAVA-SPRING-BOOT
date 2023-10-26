package org.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    public final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더의 Authorization 키 값 조회
        // HTTP Request Header에 Authorization 값이 담겨있음
        String authorizationHeader = request.getHeader(this.HEADER_AUTHORIZATION);

        // 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        logger.debug("Start Filter Token: " + token);
        // 가져온 토큰이 유효한지 확인하고 유효하면 인증 정보 설정
        if (this.jwtTokenProvider.validToken(token)) {
            Authentication authentication = this.jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("SecurityContextHolder에 토큰 저장: " + token);
        } else {
            logger.error("토큰이 유효하지 않습니다. token: " + token);
        }

        filterChain.doFilter(request, response);
    }

    // "Bearer " 값을 뺸 토큰값만 반환함.
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(this.TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
