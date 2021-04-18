package com.example.messagingstompwebsocket.model.dto;

import com.example.messagingstompwebsocket.model.enums.MessageType;
import lombok.Data;

@Data
public class ErrorDTO {
    private MessageType type = MessageType.ERROR;
    private String message;
}
