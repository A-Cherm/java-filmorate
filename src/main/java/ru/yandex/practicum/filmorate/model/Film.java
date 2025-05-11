package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private Set<Integer> likes;
    private List<Genre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration,
                Mpa filmRating, Set<Integer> likes, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = filmRating;
        this.likes = Objects.requireNonNullElseGet(likes, HashSet::new);
        this.genres = Objects.requireNonNullElseGet(genres, ArrayList::new);
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (description != null && description.length() > 200) {
            throw new ValidationException("Длина описания не может превышать 200 символов");
        }
        if (releaseDate == null
                || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода не может быть раньше 28 декабря 1895го года");
        }
        if (duration <= 0) {
            throw new ValidationException("Длительность фильма должна быть положительной");
        }
    }
}
