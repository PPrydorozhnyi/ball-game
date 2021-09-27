package com.trp.ballgame.model.dto;

import com.trp.ballgame.model.enums.MessageType;
import java.util.List;
import lombok.Data;

@Data
public class InitDto {
  private MessageType type = MessageType.INIT;
  private List<List<String>> currentChain;
  private List<Integer> totalPasses;
  private List<String> players;
  private int chainNumber;
  private int estimate;
}
