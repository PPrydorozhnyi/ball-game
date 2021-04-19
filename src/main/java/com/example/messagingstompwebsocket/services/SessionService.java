package com.example.messagingstompwebsocket.services;

import com.example.messagingstompwebsocket.model.dto.RoundDTO;
import com.example.messagingstompwebsocket.model.dto.SessionDTO;
import com.example.messagingstompwebsocket.model.entities.Round;
import com.example.messagingstompwebsocket.model.entities.Session;
import com.example.messagingstompwebsocket.model.enums.MessageType;
import com.example.messagingstompwebsocket.repository.RoundRepository;
import com.example.messagingstompwebsocket.repository.SessionRepository;
import com.example.messagingstompwebsocket.timers.RoundFinishTask;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoundRepository roundRepository;
    private final NotificationService notificationService;

    public Session createSession(SessionDTO session) {
        Session newSession = new Session();
        final var players = session.getPlayers().stream()
            .filter(name -> !ObjectUtils.isEmpty(name))
            .collect(Collectors.toList());
        newSession.setPlayers(players);
        newSession.setEstimated(session.getEstimated());
        return sessionRepository.save(newSession);
    }

    public Round createRound(RoundDTO roundDTO, int minutes) {
        Round newRound = new Round();
        newRound.setSessionId(roundDTO.getSessionId());

        List<String> roundPlay = new ArrayList<>();
        roundPlay.add(roundDTO.getPlayersName());

        List<List<String>> chain = new ArrayList<>();
        chain.add(roundPlay);

        newRound.setChain(chain);
        newRound = roundRepository.save(newRound);

        final var roundTimer =
                new RoundFinishTask(sessionRepository, notificationService, roundRepository,
                    roundDTO.getSessionId());
        final var localDateTime = LocalDateTime.now().plusMinutes(minutes);
        Date twoSecondsLaterAsDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(roundTimer, twoSecondsLaterAsDate);

        return newRound;
    }

    public RoundDTO gamePlay(RoundDTO input) {
        Integer sessionId = input.getSessionId();
        Session session = sessionRepository.getOne(sessionId);
        int totalPlayers = session.getPlayers().size();

        Round activeRound = session.getActiveRound();

        if (activeRound == null) {

            session.setActiveRound(createRound(input, totalPlayers < 6 ? 1 : 3));
            sessionRepository.save(session);

            return new RoundDTO(true, MessageType.BUTTON_PUSH, input.getPlayersName());

        } else {

            List<List<String>> chain = activeRound.getChain();
            List<String> currentChain = chain.get(chain.size() - 1);

            if (currentChain.size() < totalPlayers) {

                if (checkChain(chain, currentChain.get(currentChain.size() - 1), input.getPlayersName())) {

                    currentChain.add(input.getPlayersName());
                    roundRepository.save(activeRound);

                    if (currentChain.size() == totalPlayers) {
                        final var roundDTO = new RoundDTO(true, MessageType.ROUND_END,
                            input.getPlayersName());
                        roundDTO.setChain(chain);
                        return roundDTO;
                    } else {
                        return new RoundDTO(true, MessageType.BUTTON_PUSH,
                            input.getPlayersName());
                    }

                } else {
                    return new RoundDTO(false, MessageType.BUTTON_PUSH,
                        input.getPlayersName());
                }

            } else {
                if (input.getPlayersName().equals(chain.get(0).get(0))) {
                    List<String> newChain = new ArrayList<>();
                    newChain.add(input.getPlayersName());

                    chain.add(newChain);
                    roundRepository.save(activeRound);

                    return new RoundDTO(true, MessageType.BUTTON_PUSH,
                        input.getPlayersName());
                } else {
                    return new RoundDTO(false, MessageType.BUTTON_PUSH,
                        input.getPlayersName());
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
