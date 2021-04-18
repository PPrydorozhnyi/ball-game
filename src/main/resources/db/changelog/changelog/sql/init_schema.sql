CREATE TABLE pr_session
(
    session_id SERIAL PRIMARY KEY,
    players JSONB,
    estimated INTEGER,
    active_round_id INTEGER,
    result INTEGER
);

CREATE TABLE pr_round
(
    round_id   SERIAL PRIMARY KEY,
    chain      JSONB,
    session_id INTEGER

)
