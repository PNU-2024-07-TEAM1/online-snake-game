package com.project.project1.game;

import com.project.project1.member.MemberService;
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
    private boolean isTurn = false;
    private String direction;
    private boolean grow;
    private MemberService memberService;
    private String username;

    public Snake(Integer memberId, MemberService memberService) throws Exception {
        this.memberService = memberService;
        Random random = new Random();
        this.grow = false;
        int rand_x = random.nextInt(50, 200);
        int rand_y = random.nextInt(50, 200);
        this.memberId = memberId;
        this.snakeLength = 3;
        this.snakeNodePlaces = new ArrayList<>();
        for (int i = 0; i<this.snakeLength; i++){
            snakeNodePlaces.add(new Point(rand_x + i, rand_y));
        }
        this.direction = Direction.LEFT.getString();
        this.username = memberService.getMember(memberId).getUsername();
    }

    void gameOver() throws Exception {
        for (Point point : snakeNodePlaces){
            Experience experience = new Experience();
            experience.setPosition(point);
            GameService.experiences.add(
                experience
            );
        }
        memberService.reflectScore(memberId, snakeLength - 3);
        this.isAlive = false;
        GameService.snakes.remove(this);
    }

    void update() throws Exception {
        if (memberId == 2){
            Random random = new Random();
            int directionInt = random.nextInt(6);
            if (directionInt == 0){
                direction = turnLeft(direction);
                isTurn = true;
            } else if (directionInt == 1){
                direction = turnRight(direction);
                isTurn = true;
            }
        }
        if (isTurn){
            isTurn = false;
            return;
        }
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

        if (head.x < 0 || head.x >= 250 || head.y < 0 || head.y >= 250){
            gameOver();
        }

        if (!this.grow) {
            this.snakeNodePlaces.removeLast();
        } else {
            this.snakeLength = Math.toIntExact(snakeNodePlaces.stream().count());
            this.grow = false;
        }

        if (isCollision()){
            gameOver();
        }
    }

    public void setDirection(String direction) {
        if (direction.equals("left") && !this.direction.equals("right")) {
            this.direction = direction;
        } else if (direction.equals("right") && !this.direction.equals("left")) {
            this.direction = direction;
        } else if (direction.equals("up") && !this.direction.equals("down")) {
            this.direction = direction;
        } else if (direction.equals("down") && !this.direction.equals("up")) {
            this.direction = direction;
        }

        isTurn = true;
    }

    public boolean isCollision() {
        Point head = this.snakeNodePlaces.get(0);
        // Check collision with own body (ignore self-collision)
        for (int i = 1; i < this.snakeLength; i++) {
            if (this.snakeNodePlaces.get(i).x.equals(head.x) && this.snakeNodePlaces.get(i).y.equals(head.y)) {
                return false; // Return false to indicate no death
            }
        }
        try {
            // Check collision with other snakes
            for (int i = 0; i < GameService.experiences.stream().count(); i++) {
                Snake snake = GameService.snakes.get(i);
                if (snake.memberId.equals(this.memberId)) continue;
                for (Point segment : snake.snakeNodePlaces) {
                    if (segment.x.equals(head.x) & segment.y.equals(head.y)) {
                        return true; // Return true to indicate death
                    }
                }
            }
        } catch (Exception e){

        }
        try {
            for (int i = 0; i<GameService.experiences.stream().count(); i++) {
                Experience experience = GameService.experiences.get(i);
                if (experience.getPosition().x.equals(snakeNodePlaces.get(0).x)
                        && experience.getPosition().y.equals(snakeNodePlaces.get(0).y)) {
                    eat(experience);
                }
            }
        } catch (Exception e){

        }
        return false;
    }

    public void eat(Experience experience) {
        GameService.experiences.remove(experience);
        this.grow = true;
    }

    public Snake() {}


    private static String turnLeft(String direction) {
        switch (direction) {
            case "right":
                return "up";
            case "up":
                return "left";
            case "left":
                return "down";
            case "down":
                return "right";
            default:
                return null;
        }
    }

    private static String turnRight(String direction) {
        switch (direction) {
            case "right":
                return "down";
            case "down":
                return "left";
            case "left":
                return "up";
            case "up":
                return "right";
            default:
                return null;
        }
    }
}

