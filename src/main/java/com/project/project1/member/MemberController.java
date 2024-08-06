package com.project.project1.member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.ui.Model;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.security.Principal;


@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {


    @Autowired
    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(MemberCreationForm memberCreationForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberCreationForm memberCreationForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!memberCreationForm.getPassword1().equals(memberCreationForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            memberService.create(memberCreationForm.getUsername(),
                    memberCreationForm.getEmail(), memberCreationForm.getPassword1(), memberCreationForm.getColor());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }


    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    // 사용자 프로필 조회
    @GetMapping("/member/profile")
    public String proFile(Principal principal, Model model) {
        String username = principal.getName();
        Member member = memberService.getProfile(username).orElse(null); // 사용자 정보가 없으면 null 반환

        if (member != null) {
            // 유저네임, 이메일, 스코어만 모델에 추가
            model.addAttribute("username", member.getUsername());
            model.addAttribute("email", member.getEmail());
            model.addAttribute("score", member.getScore());
            model.addAttribute("color", member.getColor()); // 색상 정보 추가
        }

        return "member/profile";
    }

    // 사용자 프로필 업데이트
    @PostMapping("/member/update")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String color,
                                Principal principal) {
        String currentUsername = principal.getName();
        Member member = memberService.getProfile(currentUsername).orElse(null);

        if (member != null) {
            memberService.modify(member, username, email, password, color); // modify 메서드 호출
        }

        return "redirect:/member/profile"; // 수정 후 프로필 페이지로 리다이렉트
    }
}