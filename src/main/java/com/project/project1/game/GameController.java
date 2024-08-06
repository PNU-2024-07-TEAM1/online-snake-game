package com.project.project1.game;

import com.project.project1.member.Member;
import com.project.project1.member.MemberRepository;
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
    private final MemberRepository memberRepository;

    @GetMapping("snake")
    public String getGamePage(Principal principal, Model model) throws Exception {
        Member member;
        if (principal != null){
            member = memberRepository.findByLoginId(principal.getName()).get();
        }else {
            member = memberRepository.findByUsername("anonymous").get();
        }
        gameService.initGame();
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
                    memberRepository.findByLoginId(principal.getName()).get().getId()
            );
        } else {
            snake = gameService.getSnake(
                    memberRepository.findByUsername("anonymous").get().getId()
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
