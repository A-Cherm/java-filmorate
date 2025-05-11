package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
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
