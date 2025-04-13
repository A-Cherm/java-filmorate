package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> addFriend(int id, int friendId) {
        return List.of(userStorage.addFriend(id, friendId),
                userStorage.addFriend(friendId, id));
    }

    public Collection<User> deleteFriend(int id, int friendId) {
        return List.of(userStorage.deleteFriend(id, friendId),
                userStorage.deleteFriend(friendId, id));
    }

    public List<User> getFriends(int id) {
        userStorage.validateId(id);
        User user = userStorage.getUser(id);
        if (user.getFriends() == null) {
            log.info("Список друзей равен null");
            return null;
        }
        log.info("Возвращается список друзей");
        return user.getFriends()
                .stream()
                .map(userStorage::getUser)
                .toList();
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        userStorage.validateId(userId1);
        userStorage.validateId(userId2);
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        if (user1.getFriends() == null || user2.getFriends() == null) {
            log.info("Список общих друзей равен null");
            return null;
        }
        log.info("Возвращается список общих друзей");
        return user1.getFriends()
                .stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getUser)
                .toList();
    }
}
