package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsers();

    User getUser(int id);

    User createUser(User user);

    User updateUser(User user);

    User addFriend(int userId, int friendId);

    User deleteFriend(int userId, int friendId);

    void validateId(int id);
}
