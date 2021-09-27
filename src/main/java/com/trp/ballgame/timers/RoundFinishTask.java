package com.trp.ballgame.timers;

import com.trp.ballgame.model.dto.RoundDTO;
import com.trp.ballgame.model.entities.ChainRecord;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.model.entities.RoundPrimaryKey;
import com.trp.ballgame.model.enums.MessageType;
import com.trp.ballgame.repository.ChainRecordRepository;
import com.trp.ballgame.repository.RoundRepository;
import com.trp.ballgame.repository.SessionRepository;
import com.trp.ballgame.services.NotificationService;
import java.util.TimerTask;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoundFinishTask extends TimerTask {

  private final SessionRepository sessionRepository;
  private final NotificationService notificationService;
  private final RoundRepository roundRepository;
  private final ChainRecordRepository chainRecordRepository;
  private final UUID sessionId;

  @Override
  public void run() {
    final var session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));

    final var players = session.getPlayers();
    final var roundId = session.getActiveRoundId();
    final var records = chainRecordRepository.findAllById_RoundId(roundId);

    final var allChains = records.stream()
        .map(ChainRecord::getChain)
        .toList();

    final var count = allChains.stream()
        .filter(chain -> chain.size() == players.size())
        .count();

    final var roundDTO = new RoundDTO();
    roundDTO.setTotalPasses(count);
    roundDTO.setChain(allChains);
    roundDTO.setType(MessageType.FINISHED);

    final var roundPrimaryKey = new RoundPrimaryKey(roundId, sessionId);
    final var round = new Round();
    round.setId(roundPrimaryKey);
    round.setResult((int) count);

    notificationService.send(roundDTO, sessionId);
    session.setActiveRoundId(null);

    sessionRepository.save(session);
    roundRepository.save(round);
  }

}
