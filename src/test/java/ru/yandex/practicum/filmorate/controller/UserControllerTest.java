package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void controller() {
        userController = new UserController();
    }

    @Test
    public void shouldBeInvalidEmptyRequest() {
        User user = null;

        assertThrows(NotFoundException.class, () -> userController.createUser(user),
                "Пустой запрос должен приводить к ошибке");

    }

    @Test
    public void shouldGetUsers() {
        User user = new User(1, "a@b", "a", "b", LocalDate.of(2000,1,1));
        userController.createUser(user);
        Collection<User> usersFromResponse = userController.getUsers();

        assertNotNull(usersFromResponse, "Пользователи не возвращаются");
        assertEquals(1, usersFromResponse.size(), "Неверное число пользователей");

        User userFromResponse = usersFromResponse.stream().toList().getFirst();

        assertEquals(1, userFromResponse.getId(), "Неверный id");
        assertEquals("a@b", userFromResponse.getEmail(), "Неверная почта");
        assertEquals("a", userFromResponse.getLogin(), "Неверный логин");
        assertEquals("b", userFromResponse.getName(), "Неверное имя");
        assertEquals(LocalDate.of(2000, 1, 1), userFromResponse.getBirthday(),
                "Неверная дата рождения");
    }

    @Test
    public void shouldBeInvalidLogin() {
        User user = new User(1, "a@b", null, "", LocalDate.of(2000,1,1));

        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Пустой логин должен приводить к ошибке");

        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Пустой логин должен приводить к ошибке");

        user.setLogin("a a");
        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Пробел в логине должен приводить к ошибке");
    }

    @Test
    public void shouldBeInvalidEmail() {
        User user = new User(1, null, "a", "", LocalDate.of(2000,1,1));

        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Пустая почта должна приводить к ошибке");

        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Пустая почта должна приводить к ошибке");

        user.setEmail("abc");
        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Отсутсвие символа @ в почте должно приводить к ошибке");
    }

    @Test
    public void shouldBeValidBirthday() {
        User user = new User(1, "a@b", "a", "", LocalDate.now());

        userController.createUser(user);
        Collection<User> usersFromResponse = userController.getUsers();

        assertNotNull(usersFromResponse, "Пользователи не возвращаются");
        assertEquals(1, usersFromResponse.size(), "Неверное число пользователей");
    }

    @Test
    public void shouldBeInvalidBirthday() {
        User user = new User(1, "a@b", "a", "b", LocalDate.of(2200,1,1));

        assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Дата рождения не может быть в будущем");
    }

    @Test
    public void shouldBeValidEmptyName() {
        User user = new User(1, "a@b", "a", null, LocalDate.of(2000,1,1));

        userController.createUser(user);
        Collection<User> usersFromResponse = userController.getUsers();

        assertNotNull(usersFromResponse, "Пользователи не возвращаются");
        assertEquals(1, usersFromResponse.size(), "Неверное число пользователей");

        user.setName("");
        userController.createUser(user);
        usersFromResponse = userController.getUsers();

        assertNotNull(usersFromResponse, "Пользователи не возвращаются");
        assertEquals(2, usersFromResponse.size(), "Неверное число пользователей");

    }

    @Test
    public void shouldUpdateUser() {
        User user = new User(1, "a@b", "a", "b", LocalDate.of(2000,1,1));

        userController.createUser(user);

        User newUser = new User(1, "aa@b", "c", "d", LocalDate.of(2001,2,2));

        User userFromResponse = userController.updateUser(newUser);

        assertEquals(1, userFromResponse.getId(), "Неверный id пользователя");
        assertEquals("aa@b", userFromResponse.getEmail(), "Неверная почта пользователя.");
        assertEquals("c", userFromResponse.getLogin(), "Неверный логин пользователя");
        assertEquals("d", userFromResponse.getName(), "Неверное имя пользователя");
        assertEquals(LocalDate.of(2001, 2, 2), userFromResponse.getBirthday(),
                "Неверная дата рождения пользователя");
    }

    @Test
    public void shouldBeInvalidUserId() {
        User user = new User(1, "a@b", "a", "b", LocalDate.of(2000,1,1));

        userController.createUser(user);

        User newUser = new User(2, "aa@b", "c", "d", LocalDate.of(2001,2,2));

        assertThrows(NotFoundException.class, () -> userController.updateUser(newUser),
                "Нельзя обновить пользователя с несуществующим id");
    }

}