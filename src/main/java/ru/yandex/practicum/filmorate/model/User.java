package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Schema(description = "Сущность пользователя")
public class User {
    @Schema(description = "Id пользователя", example = "1")
    private int id;
    @Schema(description = "Почта пользователя", example = "vasya@mail.com")
    private String email;
    @Schema(description = "Логин пользователя", example = "vasya123")
    private String login;
    @Schema(description = "Имя пользователя", example = "Вася")
    private String name;
    @Schema(description = "Дата рождения", example = "2000-01-01T00:00:00", type = "string")
    private LocalDate birthday;
    @Schema(description = "Список id друзей", example = "[1,2,3]")
    private Set<Integer> friends;

    public User(int id, String email, String login, String name, LocalDate birthday, Set<Integer> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = Objects.requireNonNullElseGet(friends, HashSet::new);
    }

    public void validate() {
        if (login == null || login.isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелов");
        }
        if (email == null || email.isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Почта должна содержать символ @");
        }
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
    }
}
