package com.project.project1.game;

import com.project.project1.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final int DEFAULT_SNAKE_LENGTH = 3;
    private static List<Snake> snakes = new ArrayList<Snake>();



    void addSnake(Member member){
        Snake snake = new Snake(member.getId());
        snakes.add(snake);
    }

    Snake getSnake(Integer memberId) throws Exception {
        for (Snake snake : snakes){
            if (snake.getMemberId().equals(memberId)){
                return snake;
            }
        }
        throw new Exception("Snake not found");
    }
}

