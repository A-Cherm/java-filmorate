package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreMapper;

    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres";
        log.info("Возвращается список жанров");
        return jdbc.query(sqlQuery, genreMapper);
    }

    public Genre getGenre(int id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        log.info("Возвращается жанр с id = {}", id);
        try {
            return jdbc.queryForObject(sqlQuery, genreMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет жанра с id = " + id);
        }
    }
}
