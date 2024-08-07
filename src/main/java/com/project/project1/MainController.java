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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String main(Model model) throws Exception {
        List<Member> memberList = memberRepository.findAllByOrderByScoreDesc();
        model.addAttribute("memberList", memberList);

        Object princial = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomOAuth2UserDetails customOAuth2UserDetails = (CustomOAuth2UserDetails) princial;

        String curUserName = memberRepository.findByLoginId(
                customOAuth2UserDetails.getLoginId()
        ).get().getUsername();
        String curUserProfileImage = customOAuth2UserDetails.getProfileImage();
        System.out.println(curUserProfileImage);

        Member member = memberService.getMember(curUserName);

        model.addAttribute("curUserProfileImage", curUserProfileImage);
        model.addAttribute("curUserName", curUserName);

        String color = member.getColor();
        if (color == null){
            color = "#000000";
        }
        model.addAttribute("curColor", color);
        return "main";
    }

    @PostMapping("/edit")
    public String editUserSetting(Model model, @RequestParam(value = "username") String newUsername, @RequestParam(value = "color") String newColor) throws Exception {
        Object princial = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomOAuth2UserDetails customOAuth2UserDetails = (CustomOAuth2UserDetails) princial;

        String userLoginID = customOAuth2UserDetails.getLoginId();
        String curUserName = memberRepository.findByLoginId(
                userLoginID
        ).get().getUsername();
        memberService.editSetting(curUserName, newUsername, newColor);

        List<Member> memberList = memberRepository.findAllByOrderByScoreDesc();
        model.addAttribute("memberList", memberList);
        curUserName = memberRepository.findByLoginId(
                userLoginID
        ).get().getUsername();
        String curUserProfileImage = customOAuth2UserDetails.getProfileImage();
        model.addAttribute("curUserProfileImage", curUserProfileImage);
        model.addAttribute("curUserName", curUserName);

        Member member = memberRepository.findByLoginId(userLoginID).get();
        String color = member.getColor();
        if (color == null){
            color = "#000000";
        }
        model.addAttribute("curColor", color);
        return "main";
    }

    @GetMapping("/test")
    public String test() {
        return "snake/snake";
    }

}
