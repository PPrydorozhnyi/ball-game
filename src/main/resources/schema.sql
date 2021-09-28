CREATE TABLE ball_game.session
(
    id              uuid PRIMARY KEY,
    active_round_id uuid,
    estimated       int,
    players         list<text>
);

CREATE TABLE ball_game.game_round
(
    session_id uuid,
    round_id   uuid,
    estimated  int,
    result     int,
    PRIMARY KEY (session_id, round_id)
) WITH CLUSTERING ORDER BY (round_id DESC);

CREATE TABLE ball_game.chain_record
(
    round_id  uuid,
    record_id int,
    chain     list<text>,
    finished  boolean,
    version   int,
    PRIMARY KEY (round_id, record_id)
) WITH CLUSTERING ORDER BY (record_id DESC);
