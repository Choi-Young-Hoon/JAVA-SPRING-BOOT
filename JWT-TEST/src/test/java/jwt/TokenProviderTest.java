package jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.config.JwtProperties;
import org.example.config.JwtTokenProvider;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.example.test.TestJwtFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    // generateToken() 테스트
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        User testUser = this.userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());

        byte[] keyBytes = this.jwtProperties.getSecretKey().getBytes();
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = this.jwtTokenProvider.generateToken(testUser, Duration.ofDays(14));
        Long userId = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    // validToken() 검증 테스트
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        String token = TestJwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(this.jwtProperties);
        boolean result = this.jwtTokenProvider.validToken(token);
        assertThat(result).isFalse();
    }

    @DisplayName("vaildToken(): 유효한 토큰인 때에 유효성 검증에 성공한다")
    @Test
    void validToken_validToken() {
        String token = TestJwtFactory.withDefaultValues().createToken(this.jwtProperties);

        boolean result = this.jwtTokenProvider.validToken(token);

        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        String userEmail = "user@email.com";
        String token = TestJwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(this.jwtProperties);

        Authentication authentication = this.jwtTokenProvider.getAuthentication(token);

        assertThat(((UserDetails)authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }
}
