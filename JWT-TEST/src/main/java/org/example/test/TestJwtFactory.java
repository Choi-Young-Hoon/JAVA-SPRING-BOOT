package org.example.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import lombok.Getter;
import org.example.config.JwtProperties;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Getter
public class TestJwtFactory {
    private String subject = "test@email.com";
    private Date issuedAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = Collections.emptyMap();
    private Key key;

    @Builder
    public TestJwtFactory(String subject, Date issuedAt, Date expiration, Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;

        byte[] keyBytes = "iYGUmtHt6xbPkI5fjYBbjqXbk48MVhJr42qtTJYEcsk=".getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public static TestJwtFactory withDefaultValues() {
        return TestJwtFactory.builder().build();
    }

    // Jwt 라이브러리를 사용해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties) {
        return Jwts.builder()
                .setSubject(this.subject)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(this.issuedAt)
                .setExpiration(this.expiration)
                .addClaims(this.claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
