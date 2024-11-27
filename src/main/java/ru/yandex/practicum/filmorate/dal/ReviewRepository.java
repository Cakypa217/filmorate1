package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {
    private static final String BASE_QUERY = """
            SELECT r.review_id, r.isPositive, r.content, r.film_id, r.user_id, SUM(u.useful_count) AS useful
             FROM reviews r
             LEFT JOIN useful u ON u.review_id = r.review_id
            """;
    private static final String FIND_ALL_QUERY = BASE_QUERY + """
             WHERE r.film_id = ?
             GROUP BY r.review_id
             ORDER BY useful DESC
             LIMIT ?;
            """;
    private static final String FIND_ALL_WITH_LIMIT_QUERY = BASE_QUERY + """
             GROUP BY r.review_id
             ORDER BY useful DESC
             LIMIT ?;
            """;
    private static final String FIND_ALL_BY_FILM_ID_QUERY = BASE_QUERY + """
             WHERE r.film_id = ?
             GROUP BY r.review_id
             ORDER BY useful DESC;
            """;
    private static final String FIND_BY_ID_QUERY = BASE_QUERY + """
             WHERE r.review_id = ?
             GROUP BY r.review_id;
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO reviews (isPositive, content, film_id, user_id) VALUES(?, ?, ?, ?);""";
    private static final String UPDATE_QUERY = "UPDATE reviews SET isPositive = ?, content = ? WHERE review_id = ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = ?;";


    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    public List<Review> getReviewsByFilmId(Long filmId) {
        return findMany(FIND_ALL_BY_FILM_ID_QUERY, filmId);
    }

    public List<Review> getReviewsWithLimit(int count) {
        return findMany(FIND_ALL_WITH_LIMIT_QUERY, count);
    }

    public List<Review> getReviews(Long filmId, int count) {
        return findMany(FIND_ALL_QUERY, filmId, count);
    }

    public Optional<Review> getReviewById(long reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    public Review addNewReview(Review review) {
        long id = this.insert(
                INSERT_QUERY,
                review.getIsPositive(),
                review.getContent(),
                review.getFilmId(),
                review.getUserId()
        );
        review.setId(id);
        return review;
    }

    public Review updateReview(Review review) {
        update(
                UPDATE_QUERY,
                review.getIsPositive(),
                review.getContent(),
                review.getId());
        return review;
    }

    public boolean deleteReview(long reviewId) {
        return delete(DELETE_BY_ID_QUERY, reviewId);
    }
}
