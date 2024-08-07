package com.project.project1.game;

import com.project.project1.member.Member;
import com.project.project1.member.MemberRepository;
import com.project.project1.member.MemberService;
import com.project.project1.security.CustomOAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/snake")
    public String getGamePage(Model model) throws Exception {
        Member member;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomOAuth2UserDetails customOAuth2UserDetails = (CustomOAuth2UserDetails)principal;

        System.out.println(customOAuth2UserDetails.getLoginId());
        System.out.println(customOAuth2UserDetails.getUsername());

        if (customOAuth2UserDetails != null){
            member = memberRepository.findByLoginId(customOAuth2UserDetails.getLoginId()).get();
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
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        CustomOAuth2UserDetails customOAuth2UserDetails = (CustomOAuth2UserDetails)principal;

        Snake snake;
        System.out.println(principal.getName());
        if (principal != null){

            snake = gameService.getSnake(
                    memberRepository.findByUsername(principal.getName()).get().getId()
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
