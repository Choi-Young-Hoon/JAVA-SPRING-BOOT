package org.example.helloworld.controller;

import org.example.helloworld.repository.Member;
import org.example.helloworld.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController
// - 라우터 역할을 하는 애너테이션으로 라우터란 HTTP 요청과 메서드를 연결하는 장치를 말한다.
//   이 애너테이션이 있어야 클라이언트의 요청에 맞는 메서드를 실행할 수 있다.
@RestController
public class TestController {
    @Autowired // TestService 빈 주입
    TestService testService;

    // @GetMapping - HTTP GET 메소드 URL 매핑
    //               http://localhost:8080/test 접속
    @GetMapping("/test")
    public List<Member> getAllMembers() {
        List<Member> members = this.testService.getAllMembers();
        return members;
    }

}
