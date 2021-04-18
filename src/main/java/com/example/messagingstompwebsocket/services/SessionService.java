package com.example.messagingstompwebsocket.services;

import com.example.messagingstompwebsocket.model.dto.SessionDTO;
import com.example.messagingstompwebsocket.model.entities.Session;
import com.example.messagingstompwebsocket.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionService {
    private final SessionRepository sessionRepository;

    public Session createSession(SessionDTO session){
        Session newSession = new Session();
        newSession.setPlayers(session.getPlayers());
        newSession.setEstimated(session.getEstimated());
        return sessionRepository.save(newSession);
    }

    public Session editSession(Session session){
        return sessionRepository.save(session);
    }

    public Session getSession(Integer id){
        return sessionRepository.getOne(id);
    }

    public void deleteSession(Integer id){
        sessionRepository.deleteSessionById(id);
    }
}
