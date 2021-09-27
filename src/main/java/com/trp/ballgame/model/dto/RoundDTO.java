package com.trp.ballgame.model.dto;

import com.trp.ballgame.model.enums.MessageType;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoundDTO {
    private UUID sessionId;
    private String playersName;
    private List<List<String>> chain;
    private boolean success = true;
    private MessageType type;
    private long totalPasses;

    public RoundDTO(boolean success, MessageType type){
        this.success = success;
        this.type = type;
    }

    public RoundDTO(boolean success, MessageType type, String playersName){
        this.success = success;
        this.type = type;
        this.playersName = playersName;
    }
}
