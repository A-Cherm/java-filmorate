package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Возвращается список фильмов: {}", films.values());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (film == null) {
            log.error("Тело запроса добавления фильма пустое");
            throw new NotFoundException("Тело запроса пустое");
        }
        try {
            validateFilm(film);
        } catch (ValidationException e) {
            log.error("Ошибка добавления фильма. {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        film.setId(getNextId());
        log.debug("Указан id нового фильма: {}", film.getId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film == null) {
            log.error("Тело запроса обновления фильма пустое");
            throw new NotFoundException("Тело запроса пустое");
        }
        if (films.containsKey(film.getId())) {
            try {
                validateFilm(film);
            } catch (ValidationException e) {
                log.error("Ошибка обновления фильма c id = {}. {}", film.getId(), e.getMessage());
            }
            films.put(film.getId(), film);
            log.info("Обновлён фильм {}", film);
            return film;
        }
        log.error("Нет фильма с id = {}", film.getId());
        throw new NotFoundException("Нет фильма с id = " + film.getId());
    }

    private static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
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
