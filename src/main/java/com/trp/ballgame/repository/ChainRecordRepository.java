package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.ChainPrimaryKey;
import com.trp.ballgame.model.entities.ChainRecord;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface ChainRecordRepository extends CassandraRepository<ChainRecord, ChainPrimaryKey> {

  List<ChainRecord> findAllById_RoundId(UUID roundId);

  @Query("""
      select * from ball_game.chain_record
        where round_id = :roundId
        order by record_id desc
        limit 1""")
  ChainRecord findByLastRecord(UUID roundId);

}
