package com.trp.ballgame.services;

import com.trp.ballgame.model.dto.InitDto;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.repository.RoundRepository;
import com.trp.ballgame.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitService {
    private final SessionRepository sessionRepository;
    private final RoundRepository roundRepository;

    public InitDto init(Integer sessionId){
        final var session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));
        final var activeRound = session.getActiveRound();

        final var initDto = new InitDto();

        if (activeRound != null) {
            initDto.setCurrentChain(activeRound.getChain());
        }
        initDto.setPlayers(session.getPlayers());

        final var totalPasses = roundRepository.findAllBySessionId(sessionId).stream()
            .map(Round::getResult)
            .toList();

        initDto.setTotalPasses(totalPasses);
        initDto.setEstimate(session.getEstimated());

        return initDto;
    }
}
