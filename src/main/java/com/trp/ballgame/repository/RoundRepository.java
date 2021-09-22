package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.Round;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Integer> {

  List<Round> findAllBySessionId(Integer sessionId);

}
