package com.trp.ballgame.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SessionDTO {
    private List<String> players;
    private int estimated;
}
