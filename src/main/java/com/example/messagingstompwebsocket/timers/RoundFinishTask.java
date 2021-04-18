package com.example.messagingstompwebsocket.timers;

import com.example.messagingstompwebsocket.model.dto.RoundDTO;
import com.example.messagingstompwebsocket.model.enums.MessageType;
import com.example.messagingstompwebsocket.repository.SessionRepository;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RequiredArgsConstructor
public class RoundFinishTask extends TimerTask {

  private final SessionRepository sessionRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final Integer sessionId;

  @Override
  public void run() {
    final var session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));

    final var activeRound = session.getActiveRound();
    final var players = session.getPlayers();

    final var count = activeRound.getChain().stream()
        .filter(chain -> chain.size() == players.size())
        .count();

    final var roundDTO = new RoundDTO();
    roundDTO.setTotalPasses(count);
    roundDTO.setChain(activeRound.getChain());
    roundDTO.setType(MessageType.ROUND_END);

    simpMessagingTemplate.convertAndSend("/topic/greetings" + sessionId, roundDTO);
    session.setActiveRound(null);

    sessionRepository.save(session);
  }

}
