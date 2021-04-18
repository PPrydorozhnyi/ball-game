package com.example.messagingstompwebsocket.repository;

import com.example.messagingstompwebsocket.model.entities.Round;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Integer> {

  List<Round> findAllBySessionId(Integer sessionId);

}
