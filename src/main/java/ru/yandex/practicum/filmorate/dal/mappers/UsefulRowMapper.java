package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Useful;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UsefulRowMapper implements RowMapper<Useful> {
    @Override
    public Useful mapRow(ResultSet rs, int rowNum) throws SQLException {
        Useful review = new Useful();
        review.setId(rs.getLong("id"));
        review.setReviewId(rs.getLong("review_id"));
        review.setUserId(rs.getLong("user_id"));
        review.setUsefulCount(rs.getLong("useful_count"));
        return review;
    }
}
