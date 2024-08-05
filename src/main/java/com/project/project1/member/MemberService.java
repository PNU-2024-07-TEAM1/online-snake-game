package com.project.project1.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member getMember(String username) throws Exception {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()){
            return member.get();
        }
        throw new Exception("Member not found");
    }

    public List<Member> getRanking(int num) {
        List<Member> members = memberRepository.findAllByOrderByScoreDesc();

        return members.subList(0, Math.min(members.size(), num));
    }

    public Member create(String username, String email, String password)
    {
        Member member = new Member();

        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));

        memberRepository.save(member);
        return member;
    }

    public void modify(Member member, String username, String email, String password)
    {
        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));

        memberRepository.save(member);
    }

    public void delete(Member member) {
        this.memberRepository.delete(member);
    }

    // 자기 랭킹 조회
    public RankingResponse getRankingWithSelf(String username) {
        // 사용자 정보를 조회
        Optional<Member> optionalMember = memberRepository.findByUsername(username);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            List<Member> allMembers = memberRepository.findAllByOrderByScoreDesc();

            // 자기 점수
            int selfScore = member.getScore();
            // 자기 랭킹 계산
            int rank = 1;
            for (Member m : allMembers) {
                if (m.getScore() > selfScore) {
                    rank++;
                }
            }

            return new RankingResponse(rank, selfScore);
        }
        return null;
    }
}
