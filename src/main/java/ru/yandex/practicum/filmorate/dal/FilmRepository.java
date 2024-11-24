package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_FILMS = "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id";
    private static final String FIND_FILM_BY_ID = "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String CREATE_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_POPULAR_FILMS = "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "ORDER BY f.rate DESC LIMIT ?";
    private static final String CREATE_FILM = "INSERT INTO films (" +
            "name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbcTemplate, RowMapper<Film> filmRowMapper) {
        super(jdbcTemplate, filmRowMapper, Film.class);
    }

    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_FILMS, mapper);
    }

    public Optional<Film> findById(long id) {
        return findOne(FIND_FILM_BY_ID, id);
    }

    public Film create(Film film) {
        long id = insert(CREATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());

        for (Genre genre : uniqueGenres) {
            jdbc.update(CREATE_FILM_GENRES, id, genre.getId());
        }
        return film;
    }

    public void deleteFilm(long filmId) {
        jdbc.update(DELETE_FILM, filmId);
    }

    public void update(NewFilmRequest film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, RATE  = ?, mpa_id = ? WHERE film_id = ?";
        try {
            jdbc.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        } catch (Exception e) {
            throw e;
        }
        saveGenres(film);
    }

    private void saveGenres(NewFilmRequest film) {
        final Long filmId = film.getId();
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        final List<GenreDto> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<GenreDto> genreList = new ArrayList<>(genres);
        jdbc.batchUpdate(CREATE_FILM_GENRES, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genreList.get(i).getId());
            }

            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    public List<Film> getPopularFilms(int count) {
        return jdbc.query(GET_POPULAR_FILMS, mapper, count);
    }
}

