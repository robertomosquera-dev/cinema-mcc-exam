INSERT INTO movies (id, title, director, genre, duration_min, rating)
VALUES
    ('00000000-0000-4000-8000-000000000101', 'Horizonte final', 'Ana Torres', 'SCI_FI', 124, 'PG-13'),
    ('00000000-0000-4000-8000-000000000102', 'Una tarde de verano', 'Luis Vega', 'DRAMA', 105, 'PG');

INSERT INTO screenings (id, movie_id, room, show_at, total_seats, available_seats, active)
VALUES
    ('00000000-0000-4000-8000-000000000201', '00000000-0000-4000-8000-000000000101',
     'Sala 1', CURRENT_DATE + INTERVAL '7 days 18 hours', 80, 80, TRUE),
    ('00000000-0000-4000-8000-000000000202', '00000000-0000-4000-8000-000000000101',
     'Sala 2', CURRENT_DATE + INTERVAL '8 days 20 hours', 60, 60, TRUE),
    ('00000000-0000-4000-8000-000000000203', '00000000-0000-4000-8000-000000000102',
     'Sala 1', CURRENT_DATE + INTERVAL '9 days 17 hours', 80, 80, TRUE);
