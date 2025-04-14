package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    FilmService filmService;
    FilmStorage filmStorage;
    UserStorage userStorage;

    @BeforeEach
    public void newFilmService() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    @Test
    public void shouldAddLike() {
        filmStorage.createFilm(new Film(1, "a", "b",
                LocalDate.of(2000, 1, 1), 60, null));
        userStorage.createUser(new User(1, "a@b", "a", "b",
                LocalDate.of(2000,1,1), null));

        filmService.addLike(1,1);
        Film film = filmStorage.getFilm(1);

        assertNotNull(film.getLikes(), "Множество лайков не инициализировано");
        assertEquals(1, film.getLikes().size(), "Неверный размер множества лайков");
        assertTrue(film.getLikes().contains(1), "Неверное сожержание множества лайков");
    }

    @Test
    public void shouldDeleteLike() {
        filmStorage.createFilm(new Film(1, "a", "b",
                LocalDate.of(2000, 1, 1), 60, null));
        userStorage.createUser(new User(1, "a@b", "a", "b",
                LocalDate.of(2000,1,1), null));

        filmService.addLike(1,1);
        Film film = filmStorage.getFilm(1);

        assertNotNull(film.getLikes(), "Множество лайков не инициализировано");
        assertEquals(1, film.getLikes().size(), "Неверный размер множества лайков");
        assertTrue(film.getLikes().contains(1), "Неверное сожержание множества лайков");

        filmService.deleteLike(1,1);

        assertNotNull(film.getLikes(), "Множество лайков не инициализировано");
        assertEquals(0, film.getLikes().size(), "Неверный размер множества лайков");
    }

    @Test
    public void shouldReturnPopular() {
        filmStorage.createFilm(new Film(1, "a", "b",
                LocalDate.of(2000, 1, 1), 60, new HashSet<>(Set.of(1, 2, 3, 4))));
        filmStorage.createFilm(new Film(2, "b", "b",
                LocalDate.of(2000, 1, 1), 60, null));
        filmStorage.createFilm(new Film(3, "c", "c",
                LocalDate.of(2000, 1, 1), 60, new HashSet<>(Set.of(2))));

        List<Film> popularFilms = filmService.getPopular(20);

        assertNotNull(popularFilms, "Популярные фильмы не возвращаются");
        assertEquals(3, popularFilms.size(), "Неверный размер списка популярных фильмов");

        popularFilms = filmService.getPopular(1);

        assertNotNull(popularFilms, "Популярные фильмы не возвращаются");
        assertEquals(1, popularFilms.size(), "Неверный размер списка популярных фильмов");
        assertEquals(1, popularFilms.getFirst().getId(), "Неверный популярный фильм");
    }
}