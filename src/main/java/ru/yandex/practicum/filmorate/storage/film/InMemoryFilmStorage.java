package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        log.info("Возвращается список фильмов");
        log.debug("Фильмы: {}", films.values());
        return films.values();
    }

    @Override
    public Film getFilm(int id) {
        validateId(id);
        log.info("Возвращается фильм с id = {}", id);
        log.debug("Фильм: {}", films.get(id));
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        if (film == null) {
            throw new NotFoundException("Тело запроса добавления фильма пустое");
        }
        try {
            validateFilm(film);
        } catch (ValidationException e) {
            throw new ValidationException("Ошибка добавления фильма." + e.getMessage());
        }
        film.setId(getNextId());
        log.debug("Указан id нового фильма: {}", film.getId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            throw new NotFoundException("Тело запроса обновления фильма пустое");
        }
        validateId(film.getId());
        try {
            validateFilm(film);
        } catch (ValidationException e) {
            throw new ValidationException("Ошибка обновления фильма c id = " + film.getId()
                    + ". " + e.getMessage());
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм {}", film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateId(filmId);
        if (films.get(filmId).getLikes() == null) {
            films.get(filmId).setLikes(new HashSet<>(Set.of(userId)));
            log.debug("Создано новое множество лайков {} у фильма {}", films.get(filmId).getLikes(), filmId);
        } else {
            films.get(filmId).getLikes().add(userId);
        }
        log.info("Добавлен лайк от пользователя {} к фильму {}", userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateId(filmId);
        films.get(filmId).getLikes().remove(userId);
        log.info("Удалён лайк от пользователя {} к фильму {}", userId, filmId);
    }

    @Override
    public void validateId(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Нет фильма с id = " + id);
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода не может быть раньше 28 декабря 1895го года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительность фильма должна быть положительной");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
