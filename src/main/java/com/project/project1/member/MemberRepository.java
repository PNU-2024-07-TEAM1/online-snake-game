package com.project.project1.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public List<Member> findAllByOrderByScoreDesc();

    // 사용자 이름으로 회원 조회
    Optional<Member> findByUsername(String username);
    Optional<Member> findByLoginId(String loginId);
}
