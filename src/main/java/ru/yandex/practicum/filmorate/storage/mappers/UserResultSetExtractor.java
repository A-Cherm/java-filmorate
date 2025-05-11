package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Маппер для получения полной информации о пользователе, включая список друзей
@Component
public class UserResultSetExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> idToUser = new HashMap<>();

        while (rs.next()) {
            int id = rs.getInt("user_id");
            if (idToUser.containsKey(id)) {
                idToUser.get(id).getFriends().add(rs.getInt("friend_id"));
            } else {
                idToUser.put(id, new User(id,
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate(),
                        null
                ));
                int friendId = rs.getInt("friend_id");
                if (friendId != 0) {
                    idToUser.get(id).getFriends().add(friendId);
                }
            }
        }
        return new ArrayList<>(idToUser.values());
    }
}
