package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Useful;

@Repository
public class UsefulRepository extends BaseRepository<Useful> {
    private static final String DELETE_USEFUL_BY_ID_QUERY = "DELETE FROM useful WHERE review_id = ? AND user_id = ?;";
    private static final String INSERT_LIKE_QUERY = """
            INSERT INTO useful (review_id, user_id, useful_count) VALUES(?, ?, 1);""";
    private static final String INSERT_DISLIKE_QUERY = """
            INSERT INTO useful (review_id, user_id, useful_count) VALUES(?, ?, -1);""";

    public UsefulRepository(JdbcTemplate jdbc, RowMapper<Useful> mapper) {
        super(jdbc, mapper, Useful.class);
    }

    public boolean deleteUseful(Long id, Long userId) {
        return delete(DELETE_USEFUL_BY_ID_QUERY, id, userId);
    }

    public void addLikeToReview(Long id, Long userId) {
        insert(INSERT_LIKE_QUERY, id, userId);
    }

    public void addDislikeToReview(Long id, Long userId) {
        insert(INSERT_DISLIKE_QUERY, id, userId);
    }
}
