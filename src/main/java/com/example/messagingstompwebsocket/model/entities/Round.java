package com.example.messagingstompwebsocket.model.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "pr_round")
public class Round {

    @Id
    @Column(name = "round_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Type(type = EntityConst.PG_JSONB_TYPE)
    private List<List<String>> chain;

    private Integer sessionId;
}
