package com.trp.ballgame.model.entities;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("game_round")
public class Round implements Serializable {

  @PrimaryKey
  private RoundPrimaryKey id;

  private int estimated;

  @Column("result")
  @CassandraType(type = CassandraType.Name.INT)
  private int result;

}
