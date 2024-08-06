package com.project.project1.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Controller
public class MainController {
    @GetMapping("/")
    public String root(){
        return "redirect:/home";
    }
}
