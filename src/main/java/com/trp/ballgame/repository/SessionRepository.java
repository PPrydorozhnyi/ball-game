package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.Session;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface SessionRepository extends CassandraRepository<Session, UUID> {
}
