package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.*;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final ResultSetExtractor<List<Film>> extractor;

    private static final String MERGE_INTO_FILMS_GENRES = "MERGE INTO films_genres AS t " +
            "USING (VALUES (?, ?)) AS s (film_id, genre_id) " +
            "ON t.film_id = s.film_id AND t.genre_id = s.genre_id " +
            "WHEN NOT MATCHED THEN " +
            "INSERT (film_id, genre_id) VALUES (s.film_id, s.genre_id)";

    @Override
    public Collection<Film> getFilms() {
        String sqlQuery = "SELECT f.*, fl.user_id, fg.genre_id, g.genre, fr.name AS rating " +
                "FROM films AS f " +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = fg.genre_id " +
                "LEFT JOIN film_ratings AS fr ON fr.rating_id = f.film_rating_id";
        log.info("Возвращается список фильмов");
        return jdbc.query(sqlQuery, extractor);
    }

    @Override
    public Film getFilm(int id) {
        validateId(id);
        String sqlQuery = "SELECT f.*, fl.user_id, fg.genre_id, g.genre, fr.name AS rating " +
                "FROM films AS f " +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = fg.genre_id " +
                "LEFT JOIN film_ratings AS fr ON fr.rating_id = f.film_rating_id " +
                "WHERE f.film_id = ?";
        log.info("Возвращается фильм с id = {}", id);
        return jdbc.query(sqlQuery, extractor, id).getFirst();
    }

    @Override
    public Film createFilm(Film film) {
        if (film == null) {
            throw new NotFoundException("Тело запроса добавления фильма пустое");
        }
        film.validate();
        validateFilmRatingAndGenres(film);

        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, film_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, String.valueOf((film.getReleaseDate())));
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, 4);
            }
            return ps;
            }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            film.setId(id);
            log.debug("Указан id нового фильма: {}", id);
        } else {
            throw new InternalServerException("Не удалось создать фильм");
        }

        film.getGenres()
                .stream()
                .map(Genre::getId)
                .forEach(genreId -> jdbc.update(MERGE_INTO_FILMS_GENRES, film.getId(), genreId));
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            throw new NotFoundException("Тело запроса обновления фильма пустое");
        }
        film.validate();
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, film_rating_id = ? WHERE film_id = ?";

        int rowsUpdated = jdbc.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                (film.getMpa() == null) ? null : film.getMpa().getId(),
                film.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить фильм с id = " + film.getId());
        }

        film.getGenres()
                .stream()
                .map(Genre::getId)
                .forEach(genreId -> jdbc.update(MERGE_INTO_FILMS_GENRES, film.getId(), genreId));
        log.info("Обновлён фильм {}", film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateId(filmId);
        String sqlQuery = "SELECT COUNT(id) FROM films_likes WHERE film_id = ? AND user_Id = ?";

        Integer id = jdbc.queryForObject(sqlQuery, Integer.class, filmId, userId);
        if (id == null || id == 0) {
            sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";

            jdbc.update(sqlQuery, filmId, userId);
        }
        log.info("Добавлен лайк от пользователя {} к фильму {}", userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateId(filmId);
        String sqlQuery = "DELETE FROM films_likes WHERE film_Id = ? AND user_Id = ?";

        jdbc.update(sqlQuery, filmId, userId);
        log.info("Удалён лайк от пользователя {} к фильму {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery =
                "SELECT p.*, fl.user_id, fg.genre_id, g.genre, fr.name AS rating " +
                "FROM (SELECT f.*, COUNT(user_id) AS c " +
                "FROM films AS f " +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY c DESC " +
                "LIMIT ?) AS p " +
                "LEFT JOIN films_likes AS fl ON fl.film_id = p.film_id " +
                "LEFT JOIN films_genres AS fg ON fg.film_id = p.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = fg.genre_id " +
                "LEFT JOIN film_ratings AS fr ON fr.rating_id = p.film_rating_id";

        return jdbc.query(sqlQuery, extractor, count);
    }

    @Override
    public void validateId(int id) {
        String sqlQuery = "SELECT COUNT(film_id) FROM films WHERE film_id = ?";
        Integer filmId = jdbc.queryForObject(sqlQuery, Integer.class, id);
        if (filmId == null || filmId == 0) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    public void validateRatingId(int id) {
        String sqlQuery = "SELECT COUNT(rating_id) FROM film_ratings WHERE rating_id = ?";
        Integer ratingId = jdbc.queryForObject(sqlQuery, Integer.class, id);
        if (ratingId == null || ratingId == 0) {
            throw new NotFoundException("Нет рейтинга с id = " + id);
        }
    }

    public void validateGenreId(int id) {
        String sqlQuery = "SELECT COUNT(genre_id) FROM genres WHERE genre_id = ?";
        Integer ratingId = jdbc.queryForObject(sqlQuery, Integer.class, id);
        if (ratingId == null || ratingId == 0) {
            throw new NotFoundException("Нет жанра с id = " + id);
        }
    }

    public void validateFilmRatingAndGenres(Film film) {
        if (film.getMpa() != null) {
            validateRatingId(film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .forEach(this::validateGenreId);
        }
    }
}
