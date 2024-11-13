package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String CREATE_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS = "SELECT u.* FROM users u JOIN friendships f ON u.user_id = f.friend_id" +
            " WHERE f.user_id = ?";
    private static final String GET_COMMON_FRIENDS = "SELECT u.* FROM users u " +
            "JOIN friendships f1 ON u.user_id = f1.friend_id " +
            "JOIN friendships f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    public UserRepository(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper, User.class);
    }

    public List<User> findAll() {
        return jdbc.query(FIND_ALL_USERS, mapper);
    }

    public Optional<User> findById(long id) {
        return findOne(FIND_USER_BY_ID, id);
    }

    public User create(User user) {
        long id = insert(CREATE_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    public void update(User user) {
        update(UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    public void addFriend(long userId, long friendId) {
        jdbc.update(ADD_FRIEND, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        delete(DELETE_FRIEND, userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return jdbc.query(GET_FRIENDS, mapper, userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return jdbc.query(GET_COMMON_FRIENDS, mapper, userId, otherUserId);
    }
}
