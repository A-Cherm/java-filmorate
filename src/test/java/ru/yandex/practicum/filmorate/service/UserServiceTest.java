package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserStorage userStorage;
    UserService userService;

    @BeforeEach
    public void newUserService() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    public void shouldAddFriend() {
        User user1 = new User(1, "a@b", "a", "b",
                LocalDate.of(2000,1,1), null);
        User user2 = new User(2, "aa@b", "c", "d",
                LocalDate.of(2001,2,2), null);

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userService.addFriend(1,2);

        assertNotNull(user1.getFriends(), "Множество друзей не инициализировано");
        assertEquals(1, user1.getFriends().size(), "Неверный размер множества друзей");
        assertTrue(user1.getFriends().contains(2), "Неверное содержание множества друзей");
        assertNotNull(user2.getFriends(), "Множество друзей не инициализировано");
        assertEquals(0, user2.getFriends().size(), "Неверный размер множества друзей");
    }

    @Test
    public void shouldDeleteFriend() {
        User user1 = new User(1, "a@b", "a", "b",
                LocalDate.of(2000,1,1), null);
        User user2 = new User(2, "aa@b", "c", "d",
                LocalDate.of(2001,2,2), null);

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.addFriend(2, 1);
        userStorage.deleteFriend(2,1);

        assertNotNull(user1.getFriends(), "Множество друзей не инициализировано");
        assertEquals(0, user1.getFriends().size(), "Неверный размер множества друзей");
        assertNotNull(user2.getFriends(), "Множество друзей не инициализировано");
        assertEquals(0, user2.getFriends().size(), "Неверный размер множества друзей");
    }

    @Test
    public void shouldGetCommonFriends() {
        User user1 = new User(1, "a@b", "a", "b",
                LocalDate.of(2000,1,1), null);
        User user2 = new User(2, "aa@b", "c", "d",
                LocalDate.of(2001,2,2), null);
        User user3 = new User(3, "c@d", "e", "f",
                LocalDate.of(2001,3,3), null);

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userService.addFriend(1, 3);
        userService.addFriend(2, 3);

        assertNotNull(user1.getFriends(), "Множество друзей не инициализировано");
        assertEquals(1, user1.getFriends().size(), "Неверный размер множества друзей");
        assertTrue(user1.getFriends().contains(3), "Неверное содержание множества друзей");
        assertNotNull(user2.getFriends(), "Множество друзей не инициализировано");
        assertEquals(1, user2.getFriends().size(), "Неверный размер множества друзей");
        assertTrue(user2.getFriends().contains(3), "Неверное содержание множества друзей");

        List<User> commonFriends = userService.getCommonFriends(1, 2);

        assertNotNull(commonFriends, "Множество общих друзей не инициализировано");
        assertEquals(1, commonFriends.size(), "Неверный размер множества общих друзей");
        assertEquals(3, commonFriends.getFirst().getId(), "Неверный общий друг");
    }
}