package com.example.messagingstompwebsocket.model.dto;

import com.example.messagingstompwebsocket.model.enums.MessageType;
import java.util.List;
import lombok.Data;

@Data
public class InitDto {
  private MessageType type = MessageType.INIT;
  private List<List<String>> currentChain;
  private List<Long> totalPasses;
  private List<String> players;
}
