package com.trp.ballgame.model.dto;

import com.trp.ballgame.model.enums.MessageType;
import lombok.Data;

@Data
public class ErrorDTO {
    private MessageType type = MessageType.ERROR;
    private String message;
}
