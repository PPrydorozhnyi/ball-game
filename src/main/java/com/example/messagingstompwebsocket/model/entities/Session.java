package com.example.messagingstompwebsocket.model.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

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
