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
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";


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

    public void delete(long id) {
        delete(DELETE_USER, id);
    }

    public void update(User user) {
        update(UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }
}
