package com.trp.ballgame.services;

import com.trp.ballgame.model.dto.RoundDTO;
import com.trp.ballgame.model.dto.SessionDTO;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.model.entities.Session;
import com.trp.ballgame.model.enums.MessageType;
import com.trp.ballgame.repository.RoundRepository;
import com.trp.ballgame.repository.SessionRepository;
import com.trp.ballgame.timers.RoundFinishTask;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoundRepository roundRepository;
    private final NotificationService notificationService;

    public Session createSession(SessionDTO session) {
        final var newSession = new Session();
        final var players = session.players().stream()
            .filter(name -> !ObjectUtils.isEmpty(name))
            .toList();
        newSession.setPlayers(players);
        newSession.setEstimated(session.estimated());
        return sessionRepository.save(newSession);
    }

    public Round createRound(RoundDTO roundDTO, int minutes) {
        var newRound = new Round();
        newRound.setSessionId(roundDTO.getSessionId());

        final var chain = new LinkedList<List<String>>();
        chain.add(new ArrayList<>());

        newRound.setChain(chain);
        newRound = roundRepository.save(newRound);

        final var roundTimer =
                new RoundFinishTask(sessionRepository, notificationService, roundRepository,
                    roundDTO.getSessionId());
        final var localDateTime = LocalDateTime.now().plusMinutes(minutes);
        final var twoSecondsLaterAsDate =
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(roundTimer, twoSecondsLaterAsDate);

        return newRound;
    }

    public RoundDTO skip(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = sessionRepository.getById(sessionId);
        final var activeRound = session.getActiveRound();

        if (activeRound != null) {
            final var chain = activeRound.getChain();
            if (chain.size() > 1) {
                if (chain.get(chain.size() - 1).size() != session.getPlayers().size()) {
                    chain.remove(chain.size() - 1);
                    roundRepository.save(activeRound);
                }
                return new RoundDTO(true, MessageType.SKIP);
            } else {
                return new RoundDTO(false, MessageType.SKIP);
            }

        } else {
            return new RoundDTO(false, MessageType.SKIP);
        }
    }

    public RoundDTO startRound(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = sessionRepository.getById(sessionId);
        int totalPlayers = session.getPlayers().size();
        final var activeRound = session.getActiveRound();

        if (activeRound == null) {

            session.setActiveRound(createRound(input, totalPlayers < 6 ? 1 : 3));
            sessionRepository.save(session);

            return new RoundDTO(true, MessageType.START_ROUND);

        } else {
            return new RoundDTO(false, MessageType.START_ROUND);
        }
    }

    public RoundDTO gamePlay(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = sessionRepository.getById(sessionId);
        int totalPlayers = session.getPlayers().size();

        final var activeRound = session.getActiveRound();

        if (activeRound == null) {

            return new RoundDTO(false, MessageType.BUTTON_PUSH, input.getPlayersName());

        } else {

            final var chain = activeRound.getChain();
            final var currentChain = chain.get(chain.size() - 1);

            if (chain.size() == 1 && currentChain.isEmpty()) {
                currentChain.add(input.getPlayersName());
                roundRepository.save(activeRound);
                return new RoundDTO(true, MessageType.BUTTON_PUSH,
                    input.getPlayersName());
            }

            if (currentChain.size() < totalPlayers) {

                if (checkChain(chain, currentChain, input.getPlayersName())) {

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
                    final var newChain = new LinkedList<String>();
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

    private boolean checkChain(List<List<String>> chain, List<String> currentChain,
                               String outputName) {
        return !currentChain.contains(outputName) && checkTrio(chain, currentChain, outputName);
    }

    private boolean checkTrio(List<List<String>> chain, List<String> currentChain, String outputName) {
        if (chain.size() == 1) {
            return true;
        }

        if (currentChain.size() < 2) {
            return true;
        }

        final var subchain = new ArrayList<>(currentChain.subList(currentChain.size() - 2, currentChain.size()));
        subchain.add(outputName);

        for (var chn : chain) {
            if (Collections.indexOfSubList(chn, subchain) != -1) {
                return false;
            }
        }

        return true;
    }
}
