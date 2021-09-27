package com.trp.ballgame.services;

import com.trp.ballgame.model.dto.RoundDTO;
import com.trp.ballgame.model.dto.SessionDTO;
import com.trp.ballgame.model.entities.ChainPrimaryKey;
import com.trp.ballgame.model.entities.ChainRecord;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.model.entities.RoundPrimaryKey;
import com.trp.ballgame.model.entities.Session;
import com.trp.ballgame.model.enums.MessageType;
import com.trp.ballgame.repository.ChainRecordRepository;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoundRepository roundRepository;
    private final ChainRecordRepository chainRecordRepository;
    private final NotificationService notificationService;

    public Session createSession(SessionDTO session) {
        final var newSession = new Session();
        newSession.setId(UUID.randomUUID());
        final var players = session.players().stream()
            .filter(name -> !ObjectUtils.isEmpty(name))
            .toList();
        newSession.setPlayers(players);
        newSession.setEstimated(session.estimated());
        return sessionRepository.save(newSession);
    }

    public Round createRound(RoundDTO roundDTO) {
        var newRound = new Round();

        final var roundPrimaryKey = new RoundPrimaryKey();
        roundPrimaryKey.setSessionId(roundDTO.getSessionId());
        roundPrimaryKey.setRoundId(UUID.randomUUID());
        newRound.setId(roundPrimaryKey);

        newRound = roundRepository.save(newRound);

        return newRound;
    }

    private void scheduleRoundEnd(UUID sessionId, int minutes) {
        final var roundTimer =
            new RoundFinishTask(sessionRepository, notificationService, roundRepository,
                chainRecordRepository, sessionId);
        final var localDateTime = LocalDateTime.now().plusMinutes(minutes);
        final var twoSecondsLaterAsDate =
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(roundTimer, twoSecondsLaterAsDate);
    }

    private ChainRecord createChain(UUID roundId, int chainId) {
        final var chainPrimaryKey = new ChainPrimaryKey();
        chainPrimaryKey.setChainId(chainId);
        chainPrimaryKey.setRoundId(roundId);

        final var chainRecord = new ChainRecord();
        chainRecord.setId(chainPrimaryKey);

        return chainRecordRepository.save(chainRecord);
    }

    public RoundDTO skip(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));
        final var activeRoundId = session.getActiveRoundId();

        if (activeRoundId != null) {
            final var currentRecord =
                chainRecordRepository.findById_RoundIdAndFinished(activeRoundId, false);
            //cannot skip on first lap
            if (currentRecord != null && currentRecord.getId().getChainId() != 0) {
                currentRecord.setChain(new ArrayList<>(12));
                chainRecordRepository.save(currentRecord);
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
        final var session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Cannot find session by id " + sessionId));
        int totalPlayers = session.getPlayers().size();
        final var activeRound = session.getActiveRoundId();

        if (activeRound == null) {
            final var round = createRound(input);
            scheduleRoundEnd(sessionId, totalPlayers < 6 ? 1 : 3);
            final var roundId = round.getId().getRoundId();
            createChain(roundId, 0);
            session.setActiveRoundId(roundId);
            sessionRepository.save(session);

            return new RoundDTO(true, MessageType.START_ROUND);

        } else {
            return new RoundDTO(false, MessageType.START_ROUND);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public RoundDTO gamePlay(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));
        int totalPlayers = session.getPlayers().size();

        final var activeRoundId = session.getActiveRoundId();

        if (activeRoundId == null) {

            return new RoundDTO(false, MessageType.BUTTON_PUSH, input.getPlayersName());

        } else {
            final var chainRecords = chainRecordRepository.findAllById_RoundId(activeRoundId);
            final var chain = chainRecords.stream()
                .map(ChainRecord::getChain)
                .toList();
            final var currentChainRecord = chainRecords.get(chain.size() - 1);
            var currentChain = chain.get(chain.size() - 1);

            if (chain.size() == 1 && CollectionUtils.isEmpty(currentChain)) {
                currentChain = new LinkedList<>();
                currentChain.add(input.getPlayersName());
                currentChainRecord.setChain(currentChain);
                chainRecordRepository.save(currentChainRecord);
                return new RoundDTO(true, MessageType.BUTTON_PUSH,
                    input.getPlayersName());
            }

            if (currentChain.size() < totalPlayers) {

                if (checkChain(chain, currentChain, input.getPlayersName())) {

                    currentChain.add(input.getPlayersName());

                    final RoundDTO roundDTO;
                    if (currentChain.size() == totalPlayers) {
                        roundDTO = new RoundDTO(true, MessageType.ROUND_END,
                            input.getPlayersName());
                        currentChainRecord.setFinished(true);
                        roundDTO.setChain(chain);
                    } else {
                        roundDTO =  new RoundDTO(true, MessageType.BUTTON_PUSH,
                            input.getPlayersName());
                    }

                    chainRecordRepository.save(currentChainRecord);
                    return roundDTO;

                } else {
                    return new RoundDTO(false, MessageType.BUTTON_PUSH,
                        input.getPlayersName());
                }

            } else {
                if (input.getPlayersName().equals(chain.get(0).get(0))) {
                    final var id = currentChainRecord.getId();
                    final var newChainRecord = createChain(id.getRoundId(), id.getChainId() + 1);
                    final var newChain = new LinkedList<String>();
                    newChain.add(input.getPlayersName());
                    newChainRecord.setChain(newChain);
                    // todo test save in concurrency
                    chainRecordRepository.save(newChainRecord);

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
