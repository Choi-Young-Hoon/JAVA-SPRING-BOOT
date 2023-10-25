package org.example.helloworld.service;

import org.example.helloworld.repository.Member;
import org.example.helloworld.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;

    public void test() {
        // 생성
        this.memberRepository.save(new Member(1L, "A"));

        // 조회
        Optional<Member> member = this.memberRepository.findById(1L); // 단건 조회
        List<Member> allMembers = this.memberRepository.findAll(); // 전체 조회

        // 삭제
        this.memberRepository.deleteById(1L);
    }
}
