package com.example.messagingstompwebsocket.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoundDTO {
    private Integer sessionId;
    private String playersName;
    private List<List<String>> chain;
    private boolean success;
    private enum Type {
        PLAYERS,
        BUTTON_PUSH,
        FINISHED,
        ROUND_END
    }
    private Integer totalPasses;
}
