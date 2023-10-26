package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtTokenAuthenticationFilter;
import org.example.config.JwtTokenProvider;
import org.example.domain.User;
import org.example.dto.CreateAccesTokenResponse;
import org.example.dto.CreateAccessTokenRequest;
import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.service.JwtTokenService;
import org.example.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class JwtTokenContoller {
    private final JwtTokenService jwtTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    @PostMapping("/refresh-token")
    public ResponseEntity<CreateAccesTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = this.jwtTokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccesTokenResponse(newAccessToken));
    }

    // 사용자 인증 (로그인)
    @PostMapping ("/authenticate")
    public ResponseEntity<LoginResponse> authorize(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        // this.authenticationManagerBuilder.getObject().authenticate() 호출시
        // CustomUserDetailsService 클래스의 loadUserByUsername 메서드가 실행됨.
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);

        // 해당 객체를  SecurityContextHolder에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // DB 에서 User를 조회함.
        User requestUser = this.userService.findByEmail(loginRequest.getEmail());
        // jwt 토큰 생성
        String token = this.jwtTokenProvider.generateToken(requestUser, this.jwtTokenService.getExpiredDuration());

        // response 헤더에 JWT 토큰 넣어줌
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtTokenAuthenticationFilter.HEADER_AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(new LoginResponse(token), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUserInfo() {
        User user = this.userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
