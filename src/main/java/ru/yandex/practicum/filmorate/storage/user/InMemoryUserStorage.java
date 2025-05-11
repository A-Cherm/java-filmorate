package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
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
            user.validate();
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
            user.validate();
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
    public void addFriend(int userId, int friendId) {
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
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        validateId(userId);
        validateId(friendId);
        if ((users.get(userId).getFriends() != null)
            && users.get(userId).getFriends().remove(friendId)) {
            log.info("Удалён друг {} у пользователя {}", friendId, userId);
        } else {
            log.info("Нет друга {} у пользователя {}", friendId, userId);
        }
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

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
