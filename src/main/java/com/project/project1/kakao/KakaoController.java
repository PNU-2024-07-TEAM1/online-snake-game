package com.project.project1.kakao;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kakao")
public class KakaoController {

    @GetMapping("/login")
    public String login(){
        return "kakao_login";
    }
}
