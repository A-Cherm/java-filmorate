package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserResultSetExtractor;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserResultSetExtractor.class})
@Sql(scripts = {"/schema.sql", "/test-data.sql"}, executionPhase = BEFORE_TEST_CLASS)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    void testGetUsers() {
        Collection<User> users = userDbStorage.getUsers();

        assertNotNull(users, "Список пользователей не инициализирован");
        assertEquals(3, users.size(), "Неверный размер списка пользователей");
    }

    @Test
    void testGetUser() {
        User user = userDbStorage.getUser(1);

        assertEquals(1, user.getId(), "Неверный id пользователя");
        assertEquals("john@mail", user.getEmail(), "Неверный email пользователя");
        assertEquals("john123", user.getLogin(), "Неверный логин пользователя");
        assertEquals("John", user.getName(), "Неверное имя пользователя");
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthday(),
                "Неверная дата рождения пользователя");
        assertNotNull(user.getFriends(), "Список друзей не инициализирован");
        assertEquals(1, user.getFriends().size(), "Неверный размер списка друзей");
        assertTrue( user.getFriends().contains(3), "Неверный id в списке друзей");
    }

    @Test
    void testCreateUser() {
        User user = new User(3, "a@b", "asd", "Bob",
                LocalDate.of(2000, 1, 1), null);

        User newUser = userDbStorage.createUser(user);

        assertEquals(4, newUser.getId(), "Неверный id пользователя");
        assertEquals("a@b", newUser.getEmail(), "Неверный email пользователя");
        assertEquals("asd", newUser.getLogin(), "Неверный логин пользователя");
        assertEquals("Bob", newUser.getName(), "Неверное имя пользователя");
        assertEquals(LocalDate.of(2000, 1, 1), newUser.getBirthday(),
                "Неверная дата рождения пользователя");
    }

    @Test
    void testUpdateUser() {
        User user = new User(2, "a@b", "asd", "Bob",
                LocalDate.of(2000, 1, 1), null);

        User newUser = userDbStorage.updateUser(user);

        assertEquals(2, newUser.getId(), "Неверный id пользователя");
        assertEquals("a@b", newUser.getEmail(), "Неверный email пользователя");
        assertEquals("asd", newUser.getLogin(), "Неверный логин пользователя");
        assertEquals("Bob", newUser.getName(), "Неверное имя пользователя");
        assertEquals(LocalDate.of(2000, 1, 1), newUser.getBirthday(),
                "Неверная дата рождения пользователя");
    }

    @Test
    void testAddFriend() {
        userDbStorage.addFriend(1, 2);

        User user1 = userDbStorage.getUser(1);
        User user2 = userDbStorage.getUser(2);

        assertNotNull(user1.getFriends(), "Список друзей не инициализирован");
        assertEquals(2, user1.getFriends().size(), "Неверный размер списка друзей");
        assertTrue(user1.getFriends().contains(2), "Неверный id в списке друзей");

        assertNotNull(user2.getFriends(), "Список друзей не инициализирован");
        assertEquals(0, user2.getFriends().size(), "Неверный размер списка друзей");
    }

    @Test
    void testDeleteFriend() {
        userDbStorage.deleteFriend(1,3);

        User user1 = userDbStorage.getUser(1);
        User user3 = userDbStorage.getUser(3);

        assertNotNull(user1.getFriends(), "Список друзей не инициализирован");
        assertEquals(0, user1.getFriends().size(), "Неверный размер списка друзей");

        assertNotNull(user3.getFriends(), "Список друзей не инициализирован");
        assertEquals(1, user3.getFriends().size(), "Неверный размер списка друзей");
    }

    @Test
    void testValidateId() {
        User user = new User(10, "a@b", "a", "b",
                LocalDate.of(2000,1,1), null);

        assertThrows(NotFoundException.class, () -> userDbStorage.updateUser(user),
                "Нельзя обновить пользователя с несуществующим id");
    }
}