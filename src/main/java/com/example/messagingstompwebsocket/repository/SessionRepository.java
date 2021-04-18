package com.example.messagingstompwebsocket.repository;

import com.example.messagingstompwebsocket.model.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Modifying
    @Transactional
    void deleteSessionById(Integer id);
}
