package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Маппер для получения полной информации о фильме, включая названия жанров и рейтинга
@Component
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> idToFilm = new HashMap<>();

        while (rs.next()) {
            int id = rs.getInt("film_id");
            if (!idToFilm.containsKey(id)) {
                idToFilm.put(id, new Film(id,
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getInt("film_rating_id"),
                                rs.getString("rating")),
                        null,
                        null
                ));
            }
            int userId = rs.getInt("user_id");
            int genreId = rs.getInt("genre_id");
            if (userId != 0) {
                idToFilm.get(id).getLikes().add(userId);
            }
            if ((genreId != 0) &&
                    idToFilm.get(id).getGenres()
                    .stream()
                    .map(Genre::getId)
                    .noneMatch(streamId -> streamId == genreId)) {
                idToFilm.get(id).getGenres().add(new Genre(genreId, rs.getString("genre")));
            }
        }
        return new ArrayList<>(idToFilm.values());
    }
}
