package com.project.project1.member;

import com.fasterxml.jackson.annotation.JsonTypeId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uniqueId;

    private String id;

    private String password;
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
