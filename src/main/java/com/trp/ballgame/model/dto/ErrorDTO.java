package com.trp.ballgame.model.dto;

import com.trp.ballgame.model.enums.MessageType;

public record ErrorDTO(MessageType type, String message) {
    public ErrorDTO(String message) {
        this(MessageType.ERROR, message);
    }
}
