package com.trp.ballgame.controllers;

import com.trp.ballgame.model.dto.SessionDTO;
import com.trp.ballgame.services.SessionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/configure")
public class SessionRestController {

    private final SessionService sessionService;

    @CrossOrigin("*")
    @PostMapping("/create")
    public UUID createSession(@RequestBody SessionDTO sessionFrom) {
        log.info("Create session");

        final var session = sessionService.createSession(sessionFrom);
        final var sessionId = session.getId();

        log.info("Session created with id {}", sessionId);

        return sessionId;
    }
}
