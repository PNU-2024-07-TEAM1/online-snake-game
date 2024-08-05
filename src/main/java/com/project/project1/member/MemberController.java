package com.project.project1.member;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

//    private final MemberService memberService; 멤버 서비스 구현 필요

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

//        memberService.create(memberCreationForm.getMembername(),
//                memberCreationForm.getEmail(), memberCreationForm.getPassword1()); 멤버 서비스 구현 필요

        return "redirect:/";
    }
}