package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}
