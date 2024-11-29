package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String CHECK_LIKE = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String UPDATE_RATE = "UPDATE films f SET rate = (SELECT COUNT(l.user_id) FROM likes l " +
            "WHERE l.film_id = f.film_id) WHERE film_id = ?";
    private static final String GET_RECOMMENDED_FILMS_IDS = "SELECT DISTINCT l.film_id FROM likes l " +
            "WHERE l.user_id IN " +
            "(SELECT user_id FROM " +
                "(SELECT l4.user_id, count(l4.user_id) common_count, max(count(l4.user_id)) OVER () max_common " +
                "FROM likes l3 JOIN likes l4 ON l3.film_id = l4.film_id WHERE l3.user_id = ? AND l4.user_id != ? " +
                "GROUP BY l4.user_id) subquery " +
            "WHERE common_count = max_common) AND l.film_id NOT IN " +
            "(SELECT l2.film_id FROM likes l2 WHERE l2.user_id = ?)";
    private static final String IS_LIKE_EXISTS = "SELECT count(*) FROM likes WHERE film_id = ? AND user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public boolean isLikeExist(long filmId, long userId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_LIKE_EXISTS, Boolean.class, filmId, userId));
    }

    public void addLike(long filmId, long userId) {
        if (!hasLike(filmId, userId)) {
            jdbcTemplate.update(ADD_LIKE, filmId, userId);
            updateRate(filmId);
        }
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(REMOVE_LIKE, filmId, userId);
        updateRate(filmId);
    }

    private void updateRate(long filmId) {
        jdbcTemplate.update(UPDATE_RATE, filmId);
    }

    public List<Long> getRecommendedFilmsIds(Long userId) {
        return jdbcTemplate.queryForList(GET_RECOMMENDED_FILMS_IDS, Long.class, userId, userId, userId);
    }

    private boolean hasLike(long filmId, long userId) {
        Integer count = jdbcTemplate.queryForObject(CHECK_LIKE, Integer.class, filmId, userId);
        return count != null && count > 0;
    }
}
