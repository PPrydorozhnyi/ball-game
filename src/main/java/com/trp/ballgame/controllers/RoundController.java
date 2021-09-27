package com.trp.ballgame.controllers;

import com.trp.ballgame.model.dto.RoundDTO;
import com.trp.ballgame.services.InitService;
import com.trp.ballgame.services.NotificationService;
import com.trp.ballgame.services.SessionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoundController {

	private final NotificationService notificationService;
	private final InitService initService;
	private final SessionService sessionService;

	@MessageMapping("/round")
	@Retryable(value = OptimisticLockingFailureException.class,
			maxAttemptsExpression = "${app.retry.maxAttempts:5}")
	public void proceedRound(RoundDTO roundDTO,
							 @Header("simpSessionAttributes") Map<String, Object> headers) {
		switch (roundDTO.getType()) {
			case INIT -> {
				headers.put("sessionId", roundDTO.getSessionId());
				final var init = initService.init(roundDTO.getSessionId());
				notificationService.send(init, roundDTO.getSessionId());
			}
			case START_ROUND -> {
				final var start = sessionService.startRound(roundDTO);
				notificationService.send(start, roundDTO.getSessionId());
			}
			case SKIP -> {
				final var skip = sessionService.skip(roundDTO);
				notificationService.send(skip, roundDTO.getSessionId());
			}
			case BUTTON_PUSH -> {
				final var currentRound = sessionService.gamePlay(roundDTO);
				notificationService.send(currentRound, roundDTO.getSessionId());
			}
			case RETROSPECTIVE -> notificationService.send(roundDTO,
					roundDTO.getSessionId());
			default -> log.warn("Unexpected type");
		}
	}

}
