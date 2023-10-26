package jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JwtProperties;
import org.example.domain.JwtRefreshToken;
import org.example.domain.User;
import org.example.dto.CreateAccessTokenRequest;
import org.example.dto.LoginRequest;
import org.example.repository.JwtRefreshTokenRepository;
import org.example.repository.UserRepository;
import org.example.test.TestJwtFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected JwtProperties jwtProperties;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        this.userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        final String url = "/api/refresh-token";

        User testUser = this.userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String refreshToken = TestJwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(this.jwtProperties);

        this.jwtRefreshTokenRepository.save(new JwtRefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);


        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty());
    }

    @DisplayName("login: 로그인 및 현재 유저 정보를 조회한다")
    @Test
    public void login() throws Exception {

        // 테스트 유저 생성
        User testUser = this.userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        final String authenticateUrl = "/api/authenticate";
        LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), testUser.getPassword());
        final String requestBody = this.objectMapper.writeValueAsString(loginRequest);
        final ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.post(authenticateUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
        );
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
