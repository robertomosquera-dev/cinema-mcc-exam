CREATE TABLE seat_reservations (
    booking_id UUID PRIMARY KEY,
    screening_id UUID NOT NULL REFERENCES screenings(id),
    released BOOLEAN NOT NULL
);
CREATE INDEX idx_seat_reservations_screening_id ON seat_reservations(screening_id);
