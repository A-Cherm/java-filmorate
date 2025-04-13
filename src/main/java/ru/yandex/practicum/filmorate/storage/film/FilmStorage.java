package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getFilms();

    Film getFilm(int id);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    void validateId(int id);
}
