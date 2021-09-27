package com.trp.ballgame.model.entities;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Data
@PrimaryKeyClass
public class ChainPrimaryKey implements Serializable {

  @PrimaryKeyColumn(name = "round_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private UUID roundId;

  @PrimaryKeyColumn(name = "record_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
  private int chainId;

}
