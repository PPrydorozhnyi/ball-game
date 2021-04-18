package com.example.messagingstompwebsocket.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void send(Object response, Integer sessionId){
        simpMessagingTemplate.convertAndSend("/topic/greetings" + sessionId, response);
    }
}
