package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final ResultSetExtractor<List<User>> extractor;

    @Override
    public Collection<User> getUsers() {
        String sqlQuery = "SELECT u.*, uf.friend_id " +
                "FROM users AS u LEFT JOIN users_friends AS uf ON u.user_id = uf.user_id";
        return jdbc.query(sqlQuery, extractor);
    }

    @Override
    public User getUser(int id) {
        validateId(id);
        String sqlQuery = "SELECT u.*, uf.friend_id " +
                "FROM users AS u LEFT JOIN users_friends AS uf ON u.user_id = uf.user_id " +
                "WHERE u.user_id = ?";
        return jdbc.query(sqlQuery, extractor, id).getFirst();
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new NotFoundException("Тело запроса добавления пользователя пустое");
        }
        user.validate();
        setEmptyNameToLogin(user);

        String sqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
            }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            user.setId(id);
        } else {
            throw new InternalServerException("Не удалось создать пользователя");
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new NotFoundException("Тело запроса обновления пользователя пустое");
        }
        user.validate();
        setEmptyNameToLogin(user);

        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, " +
                "birthday = ? WHERE user_id = ?";

        int rowsUpdated = jdbc.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить пользователя с id = " + user.getId());
        }
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateId(userId);
        validateId(friendId);
        String sqlQuery = "INSERT INTO users_friends (user_id, friend_id) VALUES (?, ?)";

        jdbc.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        validateId(userId);
        validateId(friendId);
        String sqlQuery = "DELETE FROM users_friends WHERE user_id = ? AND friend_id = ?";

        jdbc.update(sqlQuery, userId, friendId);
    }

    @Override
    public void validateId(int id) {
        String sqlQuery = "SELECT COUNT(user_id) FROM users WHERE user_id = ?";
        Integer userId = jdbc.queryForObject(sqlQuery, Integer.class, id);
        if (userId == null || userId == 0) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    private void setEmptyNameToLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Пустое имя пользоватетеля заполнено логином {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
