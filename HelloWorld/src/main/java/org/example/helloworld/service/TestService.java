package org.example.helloworld.service;

import org.example.helloworld.repository.Member;
import org.example.helloworld.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    @Autowired
    MemberRepository memberRespository; // 빈 주입

    public List<Member> getAllMembers() {
        return memberRespository.findAll(); // 멤버 목록 얻기
    }
}
