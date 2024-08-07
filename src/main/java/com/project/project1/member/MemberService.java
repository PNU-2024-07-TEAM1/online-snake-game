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

    public void editSetting(String curUsername, String newUsername, String newColor) throws Exception {
        Member member = this.getMember(curUsername);
        member.setColor(newColor);
        member.setUsername(newUsername);
        memberRepository.save(member);
    }

    public void reflectScore(Integer id, int score) throws Exception {
        Optional<Member> optionalMember = memberRepository.findById(Long.valueOf(id));
        Member member;
        int lastScore = 0;
        if (optionalMember.isPresent()){
            member = optionalMember.get();
        } else{
            throw new Exception("member not found");
        }
        lastScore = member.getScore();
        member.setScore(Math.max(lastScore, score));
        memberRepository.save(member);
    }

    public Member getMember(String username) throws Exception {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            return member.get();
        }
        throw new Exception("Member not found");
    }

    public Member getMember(int memberId) throws Exception {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isPresent()) {
            return member.get();
        }
        throw new Exception("Member not found");
    }

    public List<Member> getRanking(int num) {
        List<Member> members = memberRepository.findAllByOrderByScoreDesc();

        return members.subList(0, Math.min(members.size(), num));
    }

    public Member create(String username, String email, String password, String color) {
        Member member = new Member();

        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setPassword(password);
        member.setColor(color);

        memberRepository.save(member);
        return member;
    }

    public Member createTestScore(String username, String email, String password, String color, Integer score) {
        Member member = new Member();

        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setPassword(password);
        member.setScore(score);
        member.setColor(color);

        memberRepository.save(member);
        return member;
    }

    public void modify(Member member, String username, String email, String password, String color) {
        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setPassword(password);
        member.setColor(color);

        memberRepository.save(member);
    }

    public void delete(Member member) {
        this.memberRepository.delete(member);
    }

    // 사용자 프로필 조회
    public Optional<Member> getProfile(String username) {
        return memberRepository.findByUsername(username);
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

    public Member OAuth2Create(String loginId, String username, String email, String provider, String providerId, String profileImage) {
        Member member = new Member();

        member.setLoginId(loginId);
        member.setUsername(username);
        member.setEmail(email);
        member.setProvider(provider);
        member.setProviderId(providerId);
        member.setProfileImage(profileImage);

        memberRepository.save(member);
        return member;
    }
}