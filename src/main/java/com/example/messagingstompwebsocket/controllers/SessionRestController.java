package com.example.messagingstompwebsocket.controllers;

import com.example.messagingstompwebsocket.model.dto.SessionDTO;
import com.example.messagingstompwebsocket.model.entities.Session;
import com.example.messagingstompwebsocket.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/configure")
public class SessionRestController {

    private static final String SESSION_CREATION_PAGE = "session/sessionCreation";

    private final SessionService sessionService;

    @GetMapping("{id}")
    public String getSession(Model model, @PathVariable Integer id){

        log.debug("getSession");

        Session session = sessionService.getSession(id);

        model.addAttribute(session);

        return SESSION_CREATION_PAGE;
    }

    @GetMapping("/create")
    public String createSession(@ModelAttribute("sessionForm") SessionDTO sessionFrom, BindingResult bindingResult){

        log.debug("Session creation");

        if(bindingResult.hasErrors()){
            return SESSION_CREATION_PAGE;
        }

        Session session = sessionService.createSession(sessionFrom);

        return "redirect:/configure/" + session.getId();
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("sessionForm") SessionDTO session, Model model){

        log.debug("editSession");

        return SESSION_CREATION_PAGE;
    }

    @DeleteMapping("/id")
    public String delete(@PathVariable Integer id){

        log.debug("deleteSession");

        sessionService.deleteSession(id);
        return "redirect:/configure";
    }
}
