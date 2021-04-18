package com.example.messagingstompwebsocket.model.dto;

import com.example.messagingstompwebsocket.model.enums.MessageType;
import java.util.List;
import lombok.Data;

@Data
public class InitDto {
  private MessageType messageType = MessageType.INIT;
  private List<List<String>> currentChain;
  private List<Integer> totalPasses;
  private List<String> players;
}
