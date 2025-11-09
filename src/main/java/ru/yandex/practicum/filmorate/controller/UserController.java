package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Получение пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК")
            })
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    @Operation(summary = "Получение друзей пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @Operation(summary = "Получение общих друзей двух пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Получение пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь создан")
            })
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    @Operation(summary = "Обновление пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @Operation(summary = "Добавление друга",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @Operation(summary = "Удаление друга",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }
}
