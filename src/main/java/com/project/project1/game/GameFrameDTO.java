package com.project.project1.game;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameFrameDTO {
    private int score;
    private List<Snake> snakes;
}
