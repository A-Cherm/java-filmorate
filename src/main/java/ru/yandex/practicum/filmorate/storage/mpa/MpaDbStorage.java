package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Mpa> ratingMapper;

    public List<Mpa> getRatings() {
        String sqlQuery = "SELECT * FROM film_ratings";
        log.info("Возвращается список рейтингов");
        return jdbc.query(sqlQuery, ratingMapper);
    }

    public Mpa getRating(int id) {
        String sqlQuery = "SELECT * FROM film_ratings WHERE rating_id = ?";
        log.info("Возвращается рейтинг с id = {}", id);
        try {
            return jdbc.queryForObject(sqlQuery, ratingMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет рейтинга с id = " + id);
        }
    }
}
