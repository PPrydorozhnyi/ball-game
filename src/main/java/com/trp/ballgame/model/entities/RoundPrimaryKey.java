package com.trp.ballgame.model.entities;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public record RoundPrimaryKey(
    @PrimaryKeyColumn(name = "session_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    UUID sessionId,
    @PrimaryKeyColumn(name = "round_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    UUID roundId
) implements Serializable {

}
