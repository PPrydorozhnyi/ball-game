package com.trp.ballgame.handler;

import com.trp.ballgame.controllers.RoundController;
import com.trp.ballgame.model.dto.ErrorDTO;
import com.trp.ballgame.services.NotificationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice(assignableTypes = RoundController.class)
@RequiredArgsConstructor
public class WebsocketExceptionHandler {

    private final NotificationService notificationService;

    @MessageExceptionHandler
    public void handleException(Exception e,
                                @Header("simpSessionAttributes") Map<String, Object> headers){
        log.error("Exception occurred: ", e);
        ErrorDTO errorDto = new ErrorDTO(e.getMessage());

        notificationService.send(errorDto, (Integer) headers.get("sessionId"));
    }
}
