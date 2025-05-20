package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilm(int id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getPopular(int count);

    void validateId(int id);
}
