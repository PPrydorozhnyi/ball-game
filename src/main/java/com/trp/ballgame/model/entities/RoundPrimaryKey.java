package com.trp.ballgame.model.entities;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;


@Data
@PrimaryKeyClass
@NoArgsConstructor
@AllArgsConstructor
public class RoundPrimaryKey implements Serializable {

  @PrimaryKeyColumn(name = "session_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private UUID sessionId;

  @PrimaryKeyColumn(name = "round_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
  private UUID roundId;

}
