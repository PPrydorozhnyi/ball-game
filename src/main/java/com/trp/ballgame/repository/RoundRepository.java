package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.model.entities.RoundPrimaryKey;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends CassandraRepository<Round, RoundPrimaryKey> {

  List<Round> findAllByIdSessionId(UUID sessionId);

}
