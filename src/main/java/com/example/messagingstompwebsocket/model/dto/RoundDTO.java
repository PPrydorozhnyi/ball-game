package com.example.messagingstompwebsocket.model.dto;

import com.example.messagingstompwebsocket.model.enums.MessageType;
import lombok.Data;

import java.util.List;

@Data
public class RoundDTO {
    private Integer sessionId;
    private String playersName;
    private List<List<String>> chain;
    private boolean success = true;
    private MessageType type;
    private long totalPasses;
}
