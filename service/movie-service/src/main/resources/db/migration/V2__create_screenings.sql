CREATE TABLE screenings (
    id              UUID         PRIMARY KEY,
    movie_id        UUID         NOT NULL,
    room            VARCHAR(255) NOT NULL,
    show_at         TIMESTAMP    NOT NULL,
    total_seats     INTEGER      NOT NULL,
    available_seats INTEGER      NOT NULL,
    active          BOOLEAN      NOT NULL,
    CONSTRAINT fk_screenings_movie FOREIGN KEY (movie_id) REFERENCES movies (id),
    CONSTRAINT chk_screenings_available_seats_nonnegative CHECK (available_seats >= 0)
);

CREATE INDEX idx_screenings_movie_id ON screenings (movie_id);
