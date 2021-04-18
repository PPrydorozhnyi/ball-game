package com.example.messagingstompwebsocket.services;

import com.example.messagingstompwebsocket.model.dto.InitDto;
import com.example.messagingstompwebsocket.repository.RoundRepository;
import com.example.messagingstompwebsocket.repository.SessionRepository;
import java.util.stream.Collectors;
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
            .filter(round -> !round.equals(activeRound))
            .map(round -> round.getChain().size())
            .collect(Collectors.toList());

        initDto.setTotalPasses(totalPasses);

        return initDto;
    }
}
