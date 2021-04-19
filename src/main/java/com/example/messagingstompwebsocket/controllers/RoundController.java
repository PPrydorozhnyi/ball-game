package com.example.messagingstompwebsocket.controllers;

import com.example.messagingstompwebsocket.model.dto.RoundDTO;
import com.example.messagingstompwebsocket.services.InitService;
import com.example.messagingstompwebsocket.services.NotificationService;
import com.example.messagingstompwebsocket.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoundController {

	private final NotificationService notificationService;
	private final InitService initService;
	private final SessionService sessionService;

	@MessageMapping("/hello")
	public void greeting(RoundDTO roundDTO, @Header("simpSessionAttributes") Map<String, Object> headers) {
		switch (roundDTO.getType()) {
			case INIT:
				headers.put("sessionId", roundDTO.getSessionId());
				final var init = initService.init(roundDTO.getSessionId());
				notificationService.send(init, roundDTO.getSessionId());
				break;
			case START_ROUND:
				final var start = sessionService.startRound(roundDTO);
				notificationService.send(start, roundDTO.getSessionId());
				break;
			case BUTTON_PUSH:
				final var currentRound = sessionService.gamePlay(roundDTO);
				notificationService.send(currentRound, roundDTO.getSessionId());
				break;
			default:
				log.warn("Unexpected type");
		}
	}

}
