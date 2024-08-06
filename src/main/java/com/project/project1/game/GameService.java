package com.project.project1.game;

import com.project.project1.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class GameService {

    private final int DEFAULT_SNAKE_LENGTH = 3;
    private static List<Snake> snakes = new ArrayList<Snake>();
    public static boolean isGameRunning = false;
    public static GameFrameDTO gameFrameDTO;

    void addSnake(Member member){
        Snake snake = new Snake(member.getId());
        snakes.add(snake);
    }

    List<Snake> getSnakes() {
        return snakes;
    }

    Snake getSnake(Integer memberId) throws Exception {
        for (Snake snake : snakes){
            if (snake.getMemberId().equals(memberId)){
                return snake;
            }
        }
        throw new Exception("Snake not found");
    }


    @Scheduled(fixedRate = 1000)
    public GameFrameDTO updateGameFrame() {
        for (Snake snake : snakes){
            snake.update();
        }
        GameFrameDTO gameFrameDTO = new GameFrameDTO();
        gameFrameDTO.setSnakes(snakes);
        gameFrameDTO.setScore(0);
        GameService.gameFrameDTO = gameFrameDTO;
        return gameFrameDTO;
    }
}

