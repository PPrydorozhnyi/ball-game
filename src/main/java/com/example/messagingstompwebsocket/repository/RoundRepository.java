package com.example.messagingstompwebsocket.repository;

import com.example.messagingstompwebsocket.model.entities.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Integer> {
}
