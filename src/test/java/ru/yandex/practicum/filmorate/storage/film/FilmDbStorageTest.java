package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, FilmResultSetExtractor.class})
@Sql(scripts = {"/schema.sql", "/test-data.sql"}, executionPhase = BEFORE_TEST_CLASS)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    void getFilms() {
        Collection<Film> films = filmDbStorage.getFilms();

        assertNotNull(films, "Список фильмов не инициализирован");
        assertEquals(3, films.size(), "Неверный размер списка фильмов");
    }

    @Test
    void getFilm() {
        Film film = filmDbStorage.getFilm(1);

        assertEquals("Film1", film.getName(), "Неверное название фильма");
        assertEquals("", film.getDescription(), "Неверное описание фильма");
        assertEquals(LocalDate.of(2000, 1, 1), film.getReleaseDate(),
                "Неверная дата выхода фильма");
        assertEquals(3, film.getMpa().getId(), "Неверный рейтинг фильма");
        assertNotNull(film.getLikes(), "Список лайков фильма не инициализирован");
        assertEquals(2, film.getLikes().size(), "Неверный размер списка лайков");
        assertNotNull(film.getGenres(), "Список жанров фильма не инициализирован");
        assertEquals(2, film.getGenres().size(), "Неверный размер списка жанров");
    }

    @Test
    void createFilm() {
        Film film = new Film(10, "newFilm", "asd", LocalDate.of(2010, 1,1),
                80, new Mpa(4, null),null, null);

        Film newFilm = filmDbStorage.createFilm(film);

        assertEquals(4, newFilm.getId(), "Неверный id нового фильма");
        assertEquals("newFilm", newFilm.getName(), "Неверное название фильма");
    }

    @Test
    void updateFilm() {
        Film film = new Film(2, "newFilm", "asd", LocalDate.of(2010, 1,1),
                80, new Mpa(4, null),null, null);

        filmDbStorage.updateFilm(film);
        Film newFilm = filmDbStorage.getFilm(2);

        assertEquals("newFilm", newFilm.getName(), "Неверное название фильма");
        assertEquals("asd", newFilm.getDescription(), "Неверное описание фильма");
        assertEquals(LocalDate.of(2010, 1, 1), newFilm.getReleaseDate(),
                "Неверная дата выхода фильма");
        assertEquals(4, newFilm.getMpa().getId(), "Неверный рейтинг фильма");
        assertNotNull(newFilm.getLikes(), "Список лайков фильма не инициализирован");
        assertEquals(0, newFilm.getLikes().size(), "Неверный размер списка лайков");
        assertNotNull(newFilm.getGenres(), "Список жанров фильма не инициализирован");
        assertEquals(1, newFilm.getGenres().size(), "Неверный размер списка жанров");
    }

    @Test
    void addLike() {
        filmDbStorage.addLike(2, 2);

        Film film = filmDbStorage.getFilm(2);

        assertNotNull(film.getLikes(), "Список лайков фильма не инициализирован");
        assertEquals(1, film.getLikes().size(), "Неверный размер списка лайков");
        assertTrue(film.getLikes().contains(2), "Неверный id в списке лайков");
    }

    @Test
    void deleteLike() {
        filmDbStorage.deleteLike(1, 1);

        Film film = filmDbStorage.getFilm(1);

        assertNotNull(film.getLikes(), "Список лайков фильма не инициализирован");
        assertEquals(1, film.getLikes().size(), "Неверный размер списка лайков");
        assertTrue(film.getLikes().contains(3), "Неверный id в списке лайков");
    }

    @Test
    void getPopular() {
        List<Film> popFilms = filmDbStorage.getPopular(1);

        assertNotNull(popFilms, "Список популярных фильмов не инициализирован");
        assertEquals(1, popFilms.size(), "Неверный размер списка популярных фильмов");

        Film film = popFilms.getFirst();

        assertEquals(1, film.getId(), "Неверный id фильма");
        assertEquals("Film1", film.getName(), "Неверное название фильма");
    }

    @Test
    void validateId() {
        Film newFilm = new Film(10, "b", "b", LocalDate.of(2010, 2, 2),
                60, null, null, null);

        assertThrows(NotFoundException.class, () -> filmDbStorage.updateFilm(newFilm),
                "Нельзя обновить фильм с несуществующим id");
    }
}