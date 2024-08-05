package com.project.project1.chat;

import com.project.project1.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String content;

    @ManyToOne
    private Member sender;
}
