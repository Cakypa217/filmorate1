package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepository {

    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String UPDATE_RATE = "UPDATE films f SET rate = (SELECT COUNT(l.user_id) FROM likes l " +
            "WHERE l.film_id = f.film_id) WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public LikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(ADD_LIKE, filmId, userId);
        updateRate(filmId);
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(REMOVE_LIKE, filmId, userId);
        updateRate(filmId);
    }

    private void updateRate(long filmId) {
        jdbcTemplate.update(UPDATE_RATE, filmId);
    }
}
