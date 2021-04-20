package com.example.messagingstompwebsocket.controllers;

import com.example.messagingstompwebsocket.model.dto.SessionDTO;
import com.example.messagingstompwebsocket.model.entities.Session;
import com.example.messagingstompwebsocket.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/create")
    public Integer createSession(@RequestBody SessionDTO sessionFrom){
        log.debug("Session creation");

        Session session = sessionService.createSession(sessionFrom);

        return session.getId();
    }
}
