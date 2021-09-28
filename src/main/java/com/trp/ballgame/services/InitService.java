package com.trp.ballgame.services;

import com.trp.ballgame.model.dto.InitDto;
import com.trp.ballgame.model.dto.TotalResult;
import com.trp.ballgame.model.entities.ChainRecord;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.repository.ChainRecordRepository;
import com.trp.ballgame.repository.RoundRepository;
import com.trp.ballgame.repository.SessionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitService {

  private final SessionRepository sessionRepository;
  private final RoundRepository roundRepository;
  private final ChainRecordRepository chainRecordRepository;

  public InitDto init(UUID sessionId) {
    final var session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));

    final var activeRoundId = session.getActiveRoundId();
    final var initDto = new InitDto();

    if (activeRoundId != null) {
      final var chains = chainRecordRepository.findAllById_RoundId(activeRoundId).stream()
          .map(ChainRecord::getChain)
          .toList();
      initDto.setCurrentChain(chains);
    }

    initDto.setPlayers(session.getPlayers());

    //todo remove
    final var totalPasses = roundRepository.findAllByIdSessionId(sessionId).stream()
        .map(Round::getResult)
        .toList();

    final var totalResult = roundRepository.getTotalResult(sessionId);
    initDto.setTotalPasses(totalPasses);
    initDto.setEstimate(session.getEstimated());
    initDto.setTotalResult(totalResult);
    return initDto;
  }

}
