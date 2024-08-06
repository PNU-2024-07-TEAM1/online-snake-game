package com.project.project1.game;

import com.project.project1.member.Member;
import com.project.project1.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@EnableScheduling
@RequiredArgsConstructor
@Controller
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final MemberService memberService;

    @GetMapping("snake")
    public String getGamePage(Principal principal, Model model) throws Exception {
        Member member;
        if (principal != null){
            member = memberService.getMember(principal.getName());
        }else {
            member = memberService.getMember("anonymous");
        }

        gameService.addSnake(
            member
        );
        model.addAttribute("memberId", member.getId());
        return "snake/snake";
    }


    @MessageMapping("/gameInput")
    public void gameInput(String direction, Principal principal) throws Exception {
        Snake snake;
        if (principal != null){
            snake = gameService.getSnake(
                    memberService.getMember(principal.getName()).getId()
            );
        } else {
            snake = gameService.getSnake(
                    memberService.getMember("anonymous").getId()
            );
        }

        snake.setDirection(direction);
    }

    @MessageMapping("/gameOutput")
    @SendTo("/topic/gameFrame")
    public GameFrameDTO sendGameFrame() {
       return  GameService.gameFrameDTO;
    }

}
