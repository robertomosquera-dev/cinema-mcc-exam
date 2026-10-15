CREATE TABLE bookings (
    id           UUID           PRIMARY KEY,
    screening_id UUID           NOT NULL,
    movie_title  VARCHAR(255)   NOT NULL,
    customer_id  UUID           NOT NULL,
    status       VARCHAR(20)    NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_bookings_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);
