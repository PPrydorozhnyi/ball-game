package com.example.messagingstompwebsocket.controllers;

import com.example.messagingstompwebsocket.model.dto.RoundDTO;
import com.example.messagingstompwebsocket.services.InitService;
import com.example.messagingstompwebsocket.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoundController {

	private final NotificationService notificationService;
	private final InitService initService;

	@MessageMapping("/hello")
	public void greeting(RoundDTO roundDTO) {
		switch (roundDTO.getType()) {
			case INIT:
				final var init = initService.init(roundDTO.getSessionId());
				notificationService.send(init, roundDTO.getSessionId());
				break;
			case BUTTON_PUSH:
				break;
			default:
				log.warn("Unexpected type");
		}
	}

}
