package com.trp.ballgame.model.entities;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("session")
public class Session implements Serializable {

  @PrimaryKey("id")
  private UUID id;

  @Column("players")
  private List<String> players;

  @Column("estimated")
  private int estimated;

  @Column("active_round_id")
  private UUID activeRoundId;
}
