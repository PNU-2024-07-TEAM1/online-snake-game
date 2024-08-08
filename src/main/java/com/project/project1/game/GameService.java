package com.project.project1.game;

import com.project.project1.member.Member;
import com.project.project1.member.MemberRepository;
import com.project.project1.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class GameService {
    public static List<Experience> experiences = new ArrayList<Experience>();
    private final int DEFAULT_SNAKE_LENGTH = 3;
    public static List<Snake> snakes = new ArrayList<Snake>();
    public static boolean isGameRunning = false;
    public static GameFrameDTO gameFrameDTO;
    private final MemberService memberService;
    public static Integer computerId;

    public void initGame() throws Exception {
        int numSnake = (int) snakes.stream().count();
        int numExperience = (int) experiences.stream().count();
        randomSpawnExperiences(Math.max(0, 50-numExperience));
        randomSpawnSnakes(Math.max(0, 20 - numSnake));
        computerId = memberService.getMember("computer").getId();
    }

    void addSnake(Member member) throws Exception {
        Snake snake = new Snake(member.getId(), memberService);
        if (!member.getUsername().equals("computer")){
            try {
                for (int i = 0; i<snakes.stream().count(); i++){
                    if (snakes.get(i).getMemberId().equals(member.getId())){
                        return;
                    }
                }
            } catch (Exception e){
                
            }
        }
        snakes.add(snake);
    }

    List<Snake> getSnakes() {
        return snakes;
    }

    void randomSpawnExperiences(int num){
        Random random = new Random();
        for (int i = 0; i<num; i++){
            Experience experience = new Experience();
            experience.setPosition(new Point(
                random.nextInt(10, 90),
                random.nextInt(10, 90)
            ));
            GameService.experiences.add(experience);
        }
    }

    void randomSpawnSnakes(int num) throws Exception {
        try {
            for (int i = 0; i<num; i++){
                addSnake(memberRepository.findByUsername("computer").get());
            }
        }
        catch (Exception e){
            Member member = new Member();
            member.setUsername("computer");
            memberRepository.save(member);
        }


    }

    Snake getSnake(Integer memberId) throws Exception {
        for (Snake snake : snakes){
            if (snake.getMemberId().equals(memberId)){
                return snake;
            }
        }
        throw new Exception("Snake not found");
    }



    @Scheduled(fixedRate = 250)
    public GameFrameDTO updateGameFrame() {
        try {
            for (int i = 0; i<snakes.stream().count(); i++){
                Snake snake = snakes.get(i);
                snake.update();
            }
        } catch (Exception e){

        }

        GameFrameDTO gameFrameDTO = new GameFrameDTO();
        gameFrameDTO.setSnakes(snakes);
        gameFrameDTO.setScore(0);
        gameFrameDTO.setExperiences(experiences);
        GameService.gameFrameDTO = gameFrameDTO;
        return gameFrameDTO;
    }

    private final MemberRepository memberRepository;
}

