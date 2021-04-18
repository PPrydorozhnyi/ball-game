package com.example.messagingstompwebsocket.model.dto;

import com.example.messagingstompwebsocket.model.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoundDTO {
    private Integer sessionId;
    private String playersName;
    private List<List<String>> chain;
    private boolean success = true;
    private MessageType type;
    private long totalPasses;

    public RoundDTO(boolean success, MessageType type){
        this.success = success;
        this.type = type;
    }
}
