package com.trp.ballgame.timers;

import com.trp.ballgame.model.dto.RoundDTO;
import com.trp.ballgame.model.enums.MessageType;
import com.trp.ballgame.repository.RoundRepository;
import com.trp.ballgame.repository.SessionRepository;
import com.trp.ballgame.services.NotificationService;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoundFinishTask extends TimerTask {

  private final SessionRepository sessionRepository;
  private final NotificationService notificationService;
  private final RoundRepository roundRepository;
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
    roundDTO.setType(MessageType.FINISHED);

    activeRound.setResult(count);

    notificationService.send(roundDTO, sessionId);
    session.setActiveRound(null);

    sessionRepository.save(session);
    roundRepository.save(activeRound);
  }

}
