package com.project.project1.game;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameFrameDTO {
    private Integer playerId;
    private boolean isAlive;
    private int score;
    private List<Snake> snakes;
}
