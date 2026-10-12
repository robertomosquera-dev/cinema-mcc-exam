CREATE TABLE movies (
    id           UUID         PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    director     VARCHAR(255) NOT NULL,
    genre        VARCHAR(20)  NOT NULL,
    duration_min INTEGER      NOT NULL,
    rating       VARCHAR(255) NOT NULL,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_movies_genre CHECK (
        genre IN ('ACTION', 'COMEDY', 'DRAMA', 'HORROR', 'THRILLER',
                  'SCI_FI', 'ANIMATION', 'ROMANCE', 'OTHER')
    )
);
