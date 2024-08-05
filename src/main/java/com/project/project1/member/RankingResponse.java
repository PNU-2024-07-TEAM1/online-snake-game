package com.project.project1.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingResponse {
    private int rank;  // 자신의 랭킹
    private int score; // 자신의 점수
}
