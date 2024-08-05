package com.project.project1.game;

import jakarta.persistence.Embeddable;

@Embeddable
public class Point{

    public Point(int x, int y){
        this.x = x; this.y = y;
    }

    public Point(Point point){
        this.x = point.x;
        this.y = point.y;
    }
    public Integer x;
    public Integer y;

    public Point() {}
}
