package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.Session;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface SessionRepository extends CassandraRepository<Session, UUID> {

  @Query("update session set active_round_id = null where id = :sessionId")
  void setActiveRoundToNull(UUID sessionId);

  @Query("select password from session where id = :sessionId")
  String getSessionPassword(UUID sessionId);

  @Query("update session set estimated = :estimated where id = :sessionId")
  void updateEstimated(UUID sessionId, int estimated);

}
