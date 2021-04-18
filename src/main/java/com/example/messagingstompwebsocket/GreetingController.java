package com.example.messagingstompwebsocket;

import com.example.messagingstompwebsocket.model.entities.NameHolder;
import com.example.messagingstompwebsocket.model.entities.NameHolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@RequiredArgsConstructor
@Controller
public class GreetingController {

	private final NameHolderRepository repository;

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message) {
		final var nameHolder = new NameHolder();
		nameHolder.setName(message.getName());
		final var save = repository.save(nameHolder);

		return new Greeting("Hello, " + HtmlUtils.htmlEscape(save.getName()) + "!");
	}

}
