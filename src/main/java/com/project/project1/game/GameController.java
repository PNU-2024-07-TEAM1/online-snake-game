package com.project.project1.game;

import com.project.project1.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final MemberService memberService;
    @GetMapping("snake")
    public String getGamePage(){
        return "game_page";
    }

    @MessageMapping("/gameInput")
    public void gameInput(String direction, Principal principal) throws Exception {
        Snake snake = gameService.getSnake(
                memberService.getMember(principal.getName()).getId()
        );
        snake.setDirection(direction);
    }

}
