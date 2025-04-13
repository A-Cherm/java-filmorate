package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    public final FilmStorage filmStorage;
    public final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(int filmId, int userId) {
        userStorage.validateId(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        userStorage.validateId(userId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getFilms()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }
}
