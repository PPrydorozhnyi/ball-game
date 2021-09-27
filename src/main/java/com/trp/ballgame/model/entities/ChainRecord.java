package com.trp.ballgame.model.entities;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("chain_record")
public class ChainRecord implements Serializable {

  @PrimaryKey
  private ChainPrimaryKey id;

  @Column("chain")
  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.VARCHAR)
  private List<String> chain;

  @Column("finished")
  @CassandraType(type = CassandraType.Name.BOOLEAN)
  private boolean finished;

  @Version
  @Column("version")
  @CassandraType(type = CassandraType.Name.INT)
  private int version;

}
