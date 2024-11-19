package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.function.Supplier;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {

    private static final String FIND_ALL_MPA = "SELECT * FROM mpa ORDER BY mpa_id";
    private static final String FIND_MPA_BY_ID = "SELECT * FROM mpa WHERE mpa_id = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    public List<Mpa> findAll() {
        return jdbc.query(FIND_ALL_MPA, mapper);
    }

    public Mpa getMpaById(Long id, Supplier<RuntimeException> exceptionSupplier) {
        try {
            return jdbc.queryForObject(FIND_MPA_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw exceptionSupplier.get();
        }
    }
}
