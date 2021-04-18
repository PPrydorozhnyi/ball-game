package com.example.messagingstompwebsocket.services;

import com.example.messagingstompwebsocket.model.dto.RoundDTO;
import com.example.messagingstompwebsocket.model.dto.SessionDTO;
import com.example.messagingstompwebsocket.model.entities.Round;
import com.example.messagingstompwebsocket.model.entities.Session;
import com.example.messagingstompwebsocket.model.enums.MessageType;
import com.example.messagingstompwebsocket.repository.RoundRepository;
import com.example.messagingstompwebsocket.repository.SessionRepository;
import com.example.messagingstompwebsocket.timers.RoundFinishTask;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoundRepository roundRepository;
    private final NotificationService notificationService;

    public Session createSession(SessionDTO session) {
        Session newSession = new Session();
        newSession.setPlayers(session.getPlayers());
        newSession.setEstimated(session.getEstimated());
        return sessionRepository.save(newSession);
    }

    public void createRound(RoundDTO roundDTO) {
        Round newRound = new Round();
        newRound.setSessionId(roundDTO.getSessionId());

        List<String> roundPlay = new ArrayList<>();
        roundPlay.add(roundDTO.getPlayersName());

        List<List<String>> chain = new ArrayList<>();
        chain.add(roundPlay);

        newRound.setChain(chain);
        roundRepository.save(newRound);

        final var roundTimer =
                new RoundFinishTask(sessionRepository, notificationService, roundDTO.getSessionId());
        final var localDateTime = LocalDateTime.now().plusMinutes(3);
        Date twoSecondsLaterAsDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(roundTimer, twoSecondsLaterAsDate);

    }

    public RoundDTO gamePlay(RoundDTO input) {
        Integer sessionId = input.getSessionId();
        Session session = sessionRepository.getOne(sessionId);
        Integer totalPlayers = session.getPlayers().size();

        Round activeRound = session.getActiveRound();

        if (activeRound == null) {

            createRound(input);

            return new RoundDTO(true, MessageType.BUTTON_PUSH);

        } else {

            List<List<String>> chain = activeRound.getChain();
            List<String> currentChain = chain.get(chain.size() - 1);

            if (currentChain.size() < totalPlayers) {

                if (checkChain(chain, currentChain.get(currentChain.size() - 1), input.getPlayersName())) {

                    currentChain.add(input.getPlayersName());
                    roundRepository.save(activeRound);

                    if (currentChain.size() == totalPlayers) {
                        return new RoundDTO(true, MessageType.ROUND_END);
                    } else {
                        return new RoundDTO(true, MessageType.BUTTON_PUSH);
                    }

                } else {
                    return new RoundDTO(false, MessageType.BUTTON_PUSH);
                }

            } else {
                if (input.getPlayersName().equals(chain.get(0).get(0))) {
                    List<String> newChain = new ArrayList<>();
                    newChain.add(input.getPlayersName());

                    chain.add(newChain);
                    roundRepository.save(activeRound);

                    return new RoundDTO(true, MessageType.BUTTON_PUSH);
                } else {
                    return new RoundDTO(false, MessageType.BUTTON_PUSH);
                }
            }
        }
    }

    private boolean checkChain(List<List<String>> chain, String inputName, String outputName) {

        for (int i = 0; i < chain.size(); ++i) {
            List<String> currentChain = chain.get(i);
            for (int j = 0; j < currentChain.size(); ++j) {
                if (currentChain.get(j).equals(inputName) && j != currentChain.size() - 1) {
                    if (currentChain.get(j + 1).equals(outputName)) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        }

        return !chain.get(chain.size() - 1).contains(outputName);
    }
}
