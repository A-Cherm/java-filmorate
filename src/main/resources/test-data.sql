INSERT INTO users (email, login, name, birthday)
VALUES ('john@mail', 'john123', 'John', '2000-01-01'),
       ('vasya@mail', 'vasya321', 'Vasya', '2010-02-02'),
       ('petya@mail', 'petya111', 'Petya', '1990-03-03');

INSERT INTO users_friends (user_id, friend_id)
VALUES (1, 3),
       (3, 1);

INSERT INTO genres (genre)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO film_ratings (name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO films (name, description, release_date, duration, film_rating_id)
VALUES ('Film1', '', '2000-01-01', 100, 3),
       ('Film2', 'just a film', '2010-02-02', 60, 5),
       ('Film3', 'abc', '1990-03-03', 180, 1);

INSERT INTO films_likes (film_id, user_id)
VALUES (1, 1),
       (1, 3),
       (3, 2);

INSERT INTO films_genres (film_id, genre_id)
VALUES (1, 5),
       (1, 2),
       (2, 1);


