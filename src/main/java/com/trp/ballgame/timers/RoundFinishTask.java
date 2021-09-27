package com.trp.ballgame.timers;

import com.trp.ballgame.model.dto.RoundDTO;
import com.trp.ballgame.model.entities.ChainRecord;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.model.entities.RoundPrimaryKey;
import com.trp.ballgame.model.entities.Session;
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
  private  final UUID sessionId;
  private final UUID roundId;
  private final int totalPlayers;

  @Override
  public void run() {
    final var records = chainRecordRepository.findAllById_RoundId(roundId);

    final var allChains = records.stream()
        .map(ChainRecord::getChain)
        .toList();

    final var count = allChains.stream()
        .filter(chain -> chain.size() == totalPlayers)
        .count();

    final var roundDTO = new RoundDTO();
    roundDTO.setTotalPasses(count);
    roundDTO.setChain(allChains);
    roundDTO.setType(MessageType.FINISHED);

    notificationService.send(roundDTO, sessionId);

    sessionRepository.setActiveRoundToNull(sessionId);
    roundRepository.setResult(sessionId, roundId, (int) count);
  }

}
