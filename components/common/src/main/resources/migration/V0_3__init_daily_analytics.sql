CREATE TABLE IF NOT EXISTS daily_analytic (
        id                              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        total_articles                  INTEGER NOT NULL,
        trending_keywords               JSONB NOT NULL,
        breaking_news                   JSONB NOT NULL,
        overall_sentiment               REAL NOT NULL,
        analysed_at                     TIMESTAMPTZ NOT NULL,
        created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);