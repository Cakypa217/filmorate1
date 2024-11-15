package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class FriendsRepository {

    private static final String ADD_FRIEND = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS = "SELECT u.* FROM users u JOIN friendships f ON u.user_id = f.friend_id" +
            " WHERE f.user_id = ?";
    private static final String GET_COMMON_FRIENDS = "SELECT u.* FROM users u " +
            "JOIN friendships f1 ON u.user_id = f1.friend_id " +
            "JOIN friendships f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> mapper;

    public FriendsRepository(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public void addFriend(long userId, long friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return jdbcTemplate.query(GET_FRIENDS, mapper, userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return jdbcTemplate.query(GET_COMMON_FRIENDS, mapper, userId, otherUserId);
    }
}
