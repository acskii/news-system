CREATE TABLE IF NOT EXISTS sources (
    id              INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            TEXT NOT NULL,
    description     TEXT NOT NULL,
    url             TEXT NOT NULL
);