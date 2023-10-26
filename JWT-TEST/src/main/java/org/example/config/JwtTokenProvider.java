package org.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.domain.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/*
    토큰을 생성하고 올바른 토큰인지 유효성 검사를 하고, 토큰에서 필요한 정볼르 가져오는 클래스
 */
@RequiredArgsConstructor
@Service
public class JwtTokenProvider implements InitializingBean {
    private final JwtProperties jwtProperties;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = this.jwtProperties.getSecretKey().getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰의 유효성 검증 메서드
    public boolean validToken(String token) {
        // 복호화를 진행하고 복호화 성공시 true 실패시 false 반환
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 토큰을 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = this.getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
               new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }

    // 토큰을 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = this.getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder() // 클레임 조회 및 복호화
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setIssuer(this.jwtProperties.getIssuer()) // application.yml jwt.issuer 값  iss: 발급자값
                .setIssuedAt(now)       // iat: 현재 시간 - 발급 일시
                .setExpiration(expiry)  // expire 되는 시간 - 만료 일시
                .setSubject(user.getEmail()) // sub: 유저의 이메일 - 토큰 제목
                .claim("id", user.getId()) // 클레임 id: 유저 id // 권한 관련 저장할 수도 있음
                // 서명: 비밀키 값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

}
