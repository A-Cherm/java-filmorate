package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Data
@Schema(description = "Сущность фильма")
public class Film {
    @Schema(description = "Id фильма", example = "1")
    private int id;
    @Schema(description = "Название фильма", example = "Отступники")
    private String name;
    @Schema(description = "Описание фильма", example = "Лучшее описание")
    private String description;
    @Schema(description = "Дата выхода", example = "2000-01-01T00:00:00", type = "string")
    private LocalDate releaseDate;
    @Schema(description = "Продолжительность в минутах", example = "90")
    private int duration;
    @Schema(description = "Возрастной рейтинг фильма")
    private Mpa mpa;
    @Schema(description = "Id пользователей, поставивших лайк", example = "[3,1,2]")
    private Set<Integer> likes;
    @Schema(description = "Жанры фильма")
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
