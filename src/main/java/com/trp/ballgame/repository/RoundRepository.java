package com.trp.ballgame.repository;

import com.trp.ballgame.model.dto.TotalResult;
import com.trp.ballgame.model.entities.Round;
import com.trp.ballgame.model.entities.RoundPrimaryKey;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends CassandraRepository<Round, RoundPrimaryKey> {

  @Query("select result from game_round limit 3")
  List<Integer> findAllByIdSessionId(UUID sessionId);

  @Query("""
      update game_round set result = :result
        where session_id = :sessionId
        and round_id = :roundId""")
  void setResult(UUID sessionId, UUID roundId, int result);

  @Query("""
      select sum(estimated) as estimated, sum(result) as result
      from game_round
      where session_id = :sessionId""")
  TotalResult getTotalResult(UUID sessionId);

}
