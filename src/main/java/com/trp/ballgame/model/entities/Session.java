package com.trp.ballgame.model.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@Table(name = "pr_session")
public class Session {

    @Id
    @Column(name = "session_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Type(type = EntityConst.PG_JSONB_TYPE)
    private List<String> players;

    private int estimated;

    @OneToOne
    @JoinColumn(name = "active_round_id", referencedColumnName = "round_id")
    private Round activeRound;
}
