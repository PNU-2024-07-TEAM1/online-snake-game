package com.project.project1.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@Setter
@Controller
public class MainController {
    @GetMapping("/")
    @ResponseBody
    public String index(){
        return "hello";
    }

}
