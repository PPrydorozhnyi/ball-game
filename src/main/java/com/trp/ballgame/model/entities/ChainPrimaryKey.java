package com.trp.ballgame.model.entities;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public record ChainPrimaryKey (
    @PrimaryKeyColumn(name = "round_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    UUID roundId,
    @PrimaryKeyColumn(name = "record_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    int chainId
) implements Serializable {

}
