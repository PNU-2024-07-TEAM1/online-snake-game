package com.project.project1;

import com.project.project1.member.Member;
import com.project.project1.member.MemberRepository;
import com.project.project1.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        // 데이터베이스에 멤버를 추가하고 Redis에 동기화합니다.
//        memberService.createTestScore("1","1","1","1",100);
//        memberService.createTestScore("2","2","1","1",400);
//        memberService.createTestScore("3","3","1","1",200);
//        memberService.createTestScore("4","4","1","1",600);
//        memberService.createTestScore("5","5","1","1",500);

        List<Member> memberList = memberRepository.findAllByOrderByScoreDesc();
        for(Member member : memberList){
            System.out.println(member.getScore());
        }
    }
}