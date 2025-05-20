MERGE INTO genres AS t
USING (VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик')) AS s (genre)
ON t.genre = s.genre
WHEN NOT MATCHED THEN
INSERT (genre) VALUES (s.genre);

MERGE INTO film_ratings AS t
USING (VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17')) AS s (ratings)
ON t.name = s.ratings
WHEN NOT MATCHED THEN
INSERT (name) VALUES (s.ratings);


