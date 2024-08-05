package com.project.project1.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Snake {

    private Integer id;
    private Integer memberId;
    private Integer snakeLength;
    private List<Point> snakeNodePlaces;
    private boolean isAlive = true;
    private String direction;
    private boolean grow;

    public Snake(Integer memberId){
        Random random = new Random();
        this.grow = false;
        int rand_x = random.nextInt(100, 4900);
        int rand_y = random.nextInt(100, 4900);
        this.memberId = memberId;
        this.snakeLength = 3;
        this.snakeNodePlaces = new ArrayList<>();
        for (int i = 0; i<this.snakeLength; i++){
            snakeNodePlaces.add(new Point(rand_x + i, rand_y));
        }
        this.direction = Direction.LEFT.getString();

    }

    void gameOver(){
        this.isAlive = false;
    }

    void update(){
        Point head = new Point(this.snakeNodePlaces.get(0));
        switch (this.direction) {
            case "left":
                head.x -= 1;
                break;
            case "right":
                head.x += 1;
                break;
            case "up":
                head.y -= 1;
                break;
            case "down":
                head.y += 1;
                break;
        }
        this.snakeNodePlaces.add(0, head);

        if (head.x < 0 || head.x >= 5000 || head.y < 0 || head.y >= 5000){
            gameOver();
        }

        if (!this.grow) {
            this.snakeNodePlaces.remove(-1);
        } else {
            this.grow = false;
        }
    }

    public void setDirection(String direction) {
        if (direction.equals("left") && this.direction.equals("right")) {
            this.direction = direction;
        } else if (direction.equals("right") && this.direction.equals("left")) {
            this.direction = direction;
        } else if (direction.equals("up") && this.direction.equals("down")) {
            this.direction = direction;
        } else if (direction.equals("down") && this.direction.equals("up")) {
            this.direction = direction;
        }
    }

    public boolean isCollision(List<Snake> snakes) {
        Point head = this.snakeNodePlaces.get(0);
        // Check collision with own body (ignore self-collision)
        for (int i = 1; i < this.snakeLength; i++) {
            if (this.snakeNodePlaces.get(i).x.equals(head.x) && this.snakeNodePlaces.get(i).y.equals(head.y)) {
                return false; // Return false to indicate no death
            }
        }
        // Check collision with other snakes
        for (Snake snake : snakes) {
            if (snake.id.equals(this.id)) continue;
            for (Point segment : snake.snakeNodePlaces) {
                if (segment.x.equals(head.x) & segment.y.equals(head.y)) {
                    return true; // Return true to indicate death
                }
            }
        }
        return false;
    }

    public void eat() {
        this.grow = true;
    }

    public Snake() {}
}

