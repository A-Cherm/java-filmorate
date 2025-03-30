package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Возвращается список пользователей: {}", users.values());
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user == null) {
            log.error("Тело запроса добавления пользователя пустое");
            throw new NotFoundException("Тело запроса пустое");
        }
        try {
            validateUser(user);
        } catch (ValidationException e) {
            log.error("Ошибка добавления пользователя. {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        setEmptyNameToLogin(user);
        user.setId(getNextId());
        log.debug("Указан id нового пользователя: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user == null) {
            log.error("Тело запроса обновления пользователя пустое");
            throw new NotFoundException("Тело запроса пустое");
        }
        if (users.containsKey(user.getId())) {
            try {
                validateUser(user);
            } catch (ValidationException e) {
                log.error("Ошибка обновления пользователя с id = {}. {}", user.getId(), e.getMessage());
                throw new ValidationException(e.getMessage());
            }
            setEmptyNameToLogin(user);
            users.put(user.getId(), user);
            log.info("Обновлён пользователь {}", user);
            return user;
        }
        log.error("Нет пользователя с id = {}", user.getId());
        throw new NotFoundException("Нет пользователя с id = " + user.getId());
    }

    private void setEmptyNameToLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Пустое имя пользоватетеля заполнено логином {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелов");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Почта должна содержать символ @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
