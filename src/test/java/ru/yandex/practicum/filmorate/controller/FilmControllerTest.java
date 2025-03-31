package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void controller() {
        filmController = new FilmController();
    }

    @Test
    public void shouldBeInvalidEmptyRequest() {
        Film film = null;

        assertThrows(NotFoundException.class, () -> filmController.createFilm(film),
                "Пустой запрос должен приводить к ошибке");
    }

    @Test
    public void shouldGetFilms() {
        Film film = new Film(1, "a", "b",
                LocalDate.of(2000, 1, 1), 30);
        filmController.createFilm(film);
        Collection<Film> filmsFromResponse = filmController.getFilms();

        assertNotNull(filmsFromResponse, "Фильмы не возвращаются");
        assertEquals(1, filmsFromResponse.size(), "Неверное число фильмов");

        Film filmFromResponse = filmsFromResponse.stream().toList().getFirst();

        assertEquals(1, filmFromResponse.getId(), "Неверный id");
        assertEquals("a", filmFromResponse.getName(), "Неверное имя");
        assertEquals("b", filmFromResponse.getDescription(), "Неверное описание");
        assertEquals(LocalDate.of(2000, 1, 1), filmFromResponse.getReleaseDate(),
                "Неверная дата выхода");
        assertEquals(30, filmFromResponse.getDuration(),
                "Неверная продолжительность фильма");
    }

    @Test
    public void shouldBeInvalidName() {
        Film film = new Film(1, null, "a",
                LocalDate.of(2000, 1, 1), 30);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film),
                "Отсутсвие имени должно приводить к ошибке");

        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.createFilm(film),
                "Пустое имя должно приводить к ошибке");
    }

    @Test
    public void shouldBeValidDescription() {
        Film film = new Film(1, "a", "a".repeat(200),
                LocalDate.of(2000, 1, 1), 30);

        filmController.createFilm(film);
        Collection<Film> filmsFromResponse = filmController.getFilms();

        assertNotNull(filmsFromResponse, "Фильмы не возвращаются");
        assertEquals(1, filmsFromResponse.size(), "Неверное число фильмов");
    }

    @Test
    public void shouldBeInvalidDescription() {
        Film film = new Film(1, "a", "a".repeat(201),
                LocalDate.of(2000, 1, 1), 30);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film),
                "Описание не может быть длиннее 200 символов");
    }

    @Test
    public void shouldBeValidDate() {
        Film film = new Film(1, "a", "a",
                LocalDate.of(1895, 12, 28), 30);

        filmController.createFilm(film);
        Collection<Film> filmsFromResponse = filmController.getFilms();

        assertNotNull(filmsFromResponse, "Фильмы не возвращаются");
        assertEquals(1, filmsFromResponse.size(), "Неверное число фильмов");
    }

    @Test
    public void shouldBeInvalidDate() {
        Film film = new Film(1, "a", "a",
                LocalDate.of(1895, 12, 27), 30);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film),
                "Дата выхода не может быть раньше 28 декабря 1895 года");
    }

    @Test
    public void shouldBeInvalidDuration() {
        Film film = new Film(1, "a", "a",
                LocalDate.of(1895, 12, 27), 0);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film),
                "Длительность должна быть положительной");
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = new Film(1, "a", "a",
                LocalDate.of(2000, 1, 1), 30);

        filmController.createFilm(film);

        Film newFilm = new Film(1, "b", "b",
                LocalDate.of(2010, 2, 2), 60);

        Film filmFromResponse = filmController.updateFilm(newFilm);

        assertEquals(1, filmFromResponse.getId(), "Неверный id фильма");
        assertEquals("b", filmFromResponse.getName(), "Неверное имя фильма");
        assertEquals("b", filmFromResponse.getDescription(), "Неверное описание фильма");
        assertEquals(LocalDate.of(2010, 2, 2), filmFromResponse.getReleaseDate(),
                "Неверная дата выхода фильма");
        assertEquals(60, filmFromResponse.getDuration(),
                "Неверная продолжительность фильма");
    }

    @Test
    public void shouldBeInvalidFilmId() {
        Film film = new Film(1, "a", "a",
                LocalDate.of(2000, 1, 1), 30);

        filmController.createFilm(film);

        Film newFilm = new Film(2, "b", "b",
                LocalDate.of(2010, 2, 2), 60);

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilm),
                "Нельзя обновить фильм с несуществующим id");
    }
}