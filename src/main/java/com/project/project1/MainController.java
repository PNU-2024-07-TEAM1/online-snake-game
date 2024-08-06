package com.project.project1;

import com.project.project1.member.Member;
import com.project.project1.member.MemberRepository;
import com.project.project1.member.MemberService;
import com.project.project1.security.CustomOAuth2UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @GetMapping("/")
    public String home() {
        return "home/home";
    }

    @GetMapping("/main")
    public String main(Model model){
        List<Member> memberList = memberRepository.findAllByOrderByScoreDesc();
        model.addAttribute("memberList", memberList);

        Object princial = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomOAuth2UserDetails customOAuth2UserDetails = (CustomOAuth2UserDetails) princial;

        String curUserName = customOAuth2UserDetails.getUsername();
        String curUserProfileImage = customOAuth2UserDetails.getProfileImage();
        System.out.println(curUserProfileImage);

        model.addAttribute("curUserProfileImage", curUserProfileImage);
        model.addAttribute("curUserName", curUserName);

        return "main";
    }

    @GetMapping("/test")
    public String test() {
        return "snake/snake";
    }
}