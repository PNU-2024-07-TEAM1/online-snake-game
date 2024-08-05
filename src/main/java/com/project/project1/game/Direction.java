package com.project.project1.game;

public enum Direction{
    RIGHT("right"),
    LEFT("left"),
    UP("up"),
    DOWN("down");
    private final String direction;
    Direction(String direction){
        this.direction = direction;
    }
    public String getString(){
        return direction;
    }
}
