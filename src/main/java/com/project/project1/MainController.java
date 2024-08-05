package com.project.project1;

import com.project.project1.member.Member;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "home/home";
    }

    @GetMapping("/test")
    public String test() {
        return "snake/snake";
    }
}
