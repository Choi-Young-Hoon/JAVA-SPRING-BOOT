package org.example.config.error;

// Jwt 인증시 필요 권한이 존재하지 않는 경우에 403 에러를 반환하는 class

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedErrorHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요 권한이 없이 접근할 때
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
