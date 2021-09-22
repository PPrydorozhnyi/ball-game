package com.trp.ballgame.controllers;

import com.trp.ballgame.model.dto.SessionDTO;
import com.trp.ballgame.model.entities.Session;
import com.trp.ballgame.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/configure")
public class SessionRestController {

    private final SessionService sessionService;

    @CrossOrigin("*")
    @PostMapping("/create")
    public Integer createSession(@RequestBody SessionDTO sessionFrom){
        log.info("Create session");

        Session session = sessionService.createSession(sessionFrom);
        final var sessionId = session.getId();

        log.info("Session created with id {}", sessionId);

        return sessionId;
    }
}
