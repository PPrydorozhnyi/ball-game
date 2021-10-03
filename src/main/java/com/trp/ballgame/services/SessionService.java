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
import org.springframework.util.StringUtils;

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
            .filter(StringUtils::hasText)
            .toList();
        newSession.setPlayers(players);
        newSession.setEstimated(session.estimated());
        newSession.setPassword(session.password());
        return sessionRepository.save(newSession);
    }

    public RoundDTO startRetrospective(RoundDTO roundDTO) {
        final var sessionPassword = sessionRepository.getSessionPassword(roundDTO.getSessionId());
        if (!sessionPassword.equals(roundDTO.getPassword())) {
            roundDTO.setSuccess(false);
        }
        return roundDTO;
    }

    public Session getSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Cannot find session " + sessionId));
    }

    public Round createRound(RoundDTO roundDTO, int estimate) {
        var newRound = new Round();

        final var roundPrimaryKey = new RoundPrimaryKey(roundDTO.getSessionId(), UUID.randomUUID());
        newRound.setId(roundPrimaryKey);
        newRound.setEstimated(estimate);

        newRound = roundRepository.save(newRound);

        return newRound;
    }

    private void scheduleRoundEnd(UUID sessionId, UUID roundId, int totalPlayers) {
        final var roundTimer =
            new RoundFinishTask(sessionRepository, notificationService, roundRepository,
                chainRecordRepository, sessionId, roundId, totalPlayers);
        final var localDateTime = LocalDateTime.now().plusMinutes(totalPlayers < 6 ? 1 : 3);
        final var twoSecondsLaterAsDate =
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(roundTimer, twoSecondsLaterAsDate);
    }

    private ChainRecord createChain(UUID roundId, int chainId) {
        final var chainPrimaryKey = new ChainPrimaryKey(roundId, chainId);

        final var chainRecord = new ChainRecord();
        chainRecord.setId(chainPrimaryKey);

        return chainRecordRepository.save(chainRecord);
    }

    public RoundDTO skip(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = getSessionById(sessionId);
        final var activeRoundId = session.getActiveRoundId();

        if (activeRoundId != null) {
            final var currentRecord =
                chainRecordRepository.findLastRecord(activeRoundId);
            //cannot skip on first lap
            if (currentRecord != null && currentRecord.getId().chainId() != 0) {
                currentRecord.setChain(new LinkedList<>());
                chainRecordRepository.save(currentRecord);
                return new RoundDTO(true, MessageType.SKIP);
            } else {
                return new RoundDTO(false, MessageType.SKIP);
            }

        } else {
            return new RoundDTO(false, MessageType.SKIP);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public RoundDTO startRound(RoundDTO input) {
        final var sessionId = input.getSessionId();
        final var session = getSessionById(sessionId);
        if (!session.getPassword().equals(input.getPassword())) {
            return new RoundDTO(false, MessageType.START_ROUND);
        }
        int totalPlayers = session.getPlayers().size();
        final var activeRound = session.getActiveRoundId();

        if (activeRound == null) {
            final var round = createRound(input, session.getEstimated());
            final var roundId = round.getId().roundId();
            createChain(roundId, 0);
            session.setActiveRoundId(roundId);
            sessionRepository.save(session);
            scheduleRoundEnd(sessionId, roundId, totalPlayers);

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

        final var activeRoundId = session.getActiveRoundId();
        final var playersName = input.getPlayersName();

        return activeRoundId == null
            ? new RoundDTO(false, MessageType.BUTTON_PUSH, playersName)
            : processRound(activeRoundId, playersName, session.getPlayers().size());
    }

    private RoundDTO processRound(UUID activeRoundId, String playersName, int totalPlayers) {
        final var chainRecords = chainRecordRepository.findAllById_RoundId(activeRoundId);
        final var chain = chainRecords.stream()
            .map(ChainRecord::getChain)
            .toList();
        final var currentChainRecord = chainRecords.get(chain.size() - 1);
        var currentChain = chain.get(chain.size() - 1);

        final RoundDTO roundDTO;
        if (CollectionUtils.isEmpty(currentChain)) {
            roundDTO = addToClearedOrNewlyCreatedChain(currentChainRecord, playersName);
        } else if (currentChain.size() < totalPlayers) {
            roundDTO = addToExistingChain(playersName, chain, currentChainRecord, currentChain,
                totalPlayers);
        } else {
            roundDTO = createNewLap(playersName, chain, currentChainRecord);
        }

        return roundDTO;
    }

    private RoundDTO addToClearedOrNewlyCreatedChain(ChainRecord currentChainRecord,
                                                     String playersName) {
        final var currentChain = new LinkedList<String>();
        currentChain.add(playersName);
        currentChainRecord.setChain(currentChain);
        chainRecordRepository.save(currentChainRecord);
        return new RoundDTO(true, MessageType.BUTTON_PUSH,
            playersName);
    }

    private RoundDTO addToExistingChain(String playersName, List<List<String>> chain,
                                        ChainRecord currentChainRecord,
                                        List<String> currentChain,
                                        int totalPlayers) {
        if (checkChain(chain, currentChain, playersName)) {

            currentChain.add(playersName);
            chainRecordRepository.save(currentChainRecord);

            final RoundDTO roundDTO;
            if (currentChain.size() == totalPlayers) {
                roundDTO = new RoundDTO(true, MessageType.ROUND_END,
                    playersName);
                roundDTO.setChain(chain);
            } else {
                roundDTO =  new RoundDTO(true, MessageType.BUTTON_PUSH,
                    playersName);
            }
            return roundDTO;

        } else {
            return new RoundDTO(false, MessageType.BUTTON_PUSH,
                playersName);
        }
    }

    private RoundDTO createNewLap(String playersName, List<List<String>> chain,
                                  ChainRecord currentChainRecord) {
        if (playersName.equals(chain.get(0).get(0))) {
            final var id = currentChainRecord.getId();
            final var newChainRecord = createChain(id.roundId(), id.chainId() + 1);
            final var newChain = new LinkedList<String>();
            newChain.add(playersName);
            newChainRecord.setChain(newChain);
            // todo test save in concurrency
            chainRecordRepository.save(newChainRecord);

            return new RoundDTO(true, MessageType.BUTTON_PUSH,
                playersName);
        } else {
            return new RoundDTO(false, MessageType.BUTTON_PUSH,
                playersName);
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
