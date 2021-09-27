package com.trp.ballgame.repository;

import com.trp.ballgame.model.entities.ChainPrimaryKey;
import com.trp.ballgame.model.entities.ChainRecord;
import com.trp.ballgame.model.entities.Session;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface ChainRecordRepository extends CassandraRepository<ChainRecord, ChainPrimaryKey> {

  List<ChainRecord> findAllById_RoundId(UUID roundId);

  ChainRecord findById_RoundIdAndFinished(UUID roundId, boolean finished);

}
