package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.config.error.JwtAccessDeniedErrorHandler;
import org.example.config.error.JwtAuthenticationEntryPointErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    // Error handler
    private final JwtAccessDeniedErrorHandler jwtAccessDeniedErrorHandler;
    private final JwtAuthenticationEntryPointErrorHandler jwtAuthenticationEntryPointErrorHandler;


    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector handlerMappingIntrospector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(handlerMappingIntrospector);
        httpSecurity.
                csrf(csrf -> csrf
                    .disable()) // CSRF 공격을 방지위해서는 활성화 해야하지만 실습을 편하게 하기위해 비활성화

                // 접근 가능한 url 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(mvcMatcherBuilder.pattern("/api/authenticate")).permitAll() // 해당 Url에 대해 누구나 접근 가능하게 한다.
                        .anyRequest().authenticated())// 모든 요청에 대해 인증

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 에러 핸들러
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(this.jwtAuthenticationEntryPointErrorHandler)
                        .accessDeniedHandler(this.jwtAccessDeniedErrorHandler))

                .apply(new JwtSecurityFilterBeforeConfig(this.jwtTokenProvider));

        return httpSecurity.build();
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
