CREATE TABLE IF NOT EXISTS articles (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title           TEXT NOT NULL,
    author          TEXT NOT NULL,
    src             INT REFERENCES sources(id) ON DELETE CASCADE,
    description     TEXT NOT NULL,
    content         TEXT NOT NULL,
    url             TEXT NOT NULL,
    published_at    TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);