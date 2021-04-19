package com.example.messagingstompwebsocket.handler;

import com.example.messagingstompwebsocket.controllers.RoundController;
import com.example.messagingstompwebsocket.model.dto.ErrorDTO;
import com.example.messagingstompwebsocket.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@Slf4j
@ControllerAdvice(assignableTypes = RoundController.class)
@RequiredArgsConstructor
public class WebsocketExceptionHandler {
    private final NotificationService notificationService;

    @MessageExceptionHandler
    public void handleException(Exception e, @Header("simpSessionAttributes") Map<String, Object> headers){
        log.error("Exception occurred: ", e);
        ErrorDTO errorDto = new ErrorDTO();
        errorDto.setMessage(e.getMessage());

        notificationService.send(errorDto, (Integer) headers.get("sessionId"));
    }
}
