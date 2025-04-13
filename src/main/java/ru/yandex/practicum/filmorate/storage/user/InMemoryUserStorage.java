package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        log.info("Возвращается список пользователей");
        log.debug("Пользователи: {}", users.values());
        return users.values();
    }

    @Override
    public User getUser(int id) {
        validateId(id);
        log.info("Возвращается пользователь с id = {}", id);
        log.debug("Пользователь: {}", users.get(id));
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new NotFoundException("Тело запроса добавления пользователя пустое");
        }
        try {
            validateUser(user);
        } catch (ValidationException e) {
            throw new ValidationException("Ошибка добавления пользователя." + e.getMessage());
        }
        setEmptyNameToLogin(user);
        user.setId(getNextId());
        log.debug("Указан id нового пользователя: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new NotFoundException("Тело запроса обновления пользователя пустое");
        }
        validateId(user.getId());
        try {
            validateUser(user);
        } catch (ValidationException e) {
            throw new ValidationException("Ошибка обновления пользователя с id = " + user.getId()
                    + ". " + e.getMessage());
        }
        setEmptyNameToLogin(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь {}", user);
        return user;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        validateId(userId);
        validateId(friendId);
        if (users.get(userId).getFriends() == null) {
            users.get(userId).setFriends(new HashSet<>(Set.of(friendId)));
            log.debug("Создано множесто друзей {} у пользователя {}",
                    users.get(userId).getFriends(), userId);
        } else {
            users.get(userId).getFriends().add(friendId);
        }
        log.info("Добавлен друг {} у пользователя {}", friendId, userId);
        return users.get(userId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        validateId(userId);
        validateId(friendId);
        if ((users.get(userId).getFriends() != null)
            && users.get(userId).getFriends().remove(friendId)) {
            log.info("Удалён друг {} у пользователя {}", friendId, userId);
        } else {
            log.info("Нет друга {} у пользователя {}", friendId, userId);
        }
        return users.get(userId);
    }

    @Override
    public void validateId(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
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
