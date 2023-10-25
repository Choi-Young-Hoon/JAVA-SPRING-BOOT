package org.example.helloworld.controller;

import org.example.helloworld.repository.Member;
import org.example.helloworld.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach // 테스트 실행 전 실행하는 메서드
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }
    @AfterEach // 테스트 실행 후 실행하는 메서드
    public void cleanup() {
        this.memberRepository.deleteAll();
    }

    @DisplayName("getAllMembers: 아티클 조회에 성공한다")
    @Test
    public void getAllMembers() throws Exception {
        final String url = "/test";
        Member savedMember = this.memberRepository.save(new Member(1L, "홍길동"));

        final ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedMember.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(savedMember.getName()));

    }
}