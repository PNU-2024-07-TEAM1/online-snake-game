package com.project.project1.member;

import com.fasterxml.jackson.annotation.JsonTypeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    //일단 보류
    //private List<String> chatting;

    private Integer score;

    // 지렁이 길이
    //private Integer earthwormLength;

    // 지렁이 노드 리스트
    //private List<Point> earthwormPlace;

    // 지렁이 방향 (-1, 0), (1, 0), (0, -1), (0, 1)
    //private Point direction;
}

class Point{
    private Integer x;
    private Integer y;
}
