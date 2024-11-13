package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {

    private static final String FIND_ALL_MPA = "SELECT * FROM mpa ORDER BY mpa_id";
    private static final String FIND_MPA_BY_ID = "SELECT * FROM mpa WHERE mpa_id = ?";
    private static final String FIND_MPA_ID_FOR_FILM = "SELECT mpa_id FROM film_mpa WHERE film_id = ?";
    private static final String UPDATE_MPA = "UPDATE film_mpa SET mpa_id = ? WHERE film_id = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    public List<Mpa> findAll() {
        return jdbc.query(FIND_ALL_MPA, mapper);
    }


    public Long getMpaForFilm(Long filmId) {
        return jdbc.queryForObject(FIND_MPA_ID_FOR_FILM, Long.class, filmId);
    }

    public Mpa getMpaById(Long id) {
        try {
            return jdbc.queryForObject(FIND_MPA_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException("MPA с id " + id + " не найден");
        }
    }

    public Mpa MpaById(Long id) {
        try {
            return jdbc.queryForObject(FIND_MPA_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с id " + id + " не найден");
        }
    }


    public void update(long film_id, long mpa_id) {
        jdbc.update(UPDATE_MPA, mpa_id, film_id);
    }
}
