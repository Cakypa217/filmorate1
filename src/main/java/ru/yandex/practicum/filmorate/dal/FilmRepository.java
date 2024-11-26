package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.DirectorQueryParams;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            "JOIN mpa m ON f.mpa_id = m.mpa_id ";
    private static final String CREATE_FILM = "INSERT INTO films (" +
            "name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_FILMS_BY_IDS = FIND_ALL_FILMS +
            " WHERE film_id IN (?)";
    private static final String CREATE_FILM_DIRECTORS = "INSERT INTO directed_by (director_id, film_id) VALUES (?, ?)";
    private static final String FIND_DIRECTORS_FILMS_BY_LIKES
            = "SELECT f.*, " +
            " m.mpa_id AS mpa_id, m.name AS mpa_name," +
            " (SELECT COUNT(1) FROM likes" +
            " WHERE film_id = f.film_id) AS cnt " +
            " FROM directed_by AS db" +
            " INNER JOIN films AS f ON db.film_id = f.film_id" +
            " INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
            " WHERE db.director_id = ?" +
            " ORDER BY cnt DESC";
    private static final String FIND_DIRECTORS_FILMS_BY_YEAR
            = "SELECT f.*," +
            " m.mpa_id AS mpa_id, m.name AS mpa_name" +
            " FROM directed_by AS db" +
            " INNER JOIN films AS f ON db.film_id = f.film_id" +
            " INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
            " WHERE db.director_id = ?" +
            " ORDER BY f.release_date";

    public FilmRepository(JdbcTemplate jdbcTemplate, RowMapper<Film> filmRowMapper) {
        super(jdbcTemplate, filmRowMapper, Film.class);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_FILMS);
    }

    public Optional<Film> findById(long id) {
        return findOne(FIND_FILM_BY_ID, id);
    }

    public List<Film> findByIds(List<Long> ids) {
        String idsString = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return findMany(FIND_FILMS_BY_IDS, idsString);
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

        Set<Director> uniqueDirectors = new HashSet<>(film.getDirectors());
        for (Director director : uniqueDirectors) {
            jdbc.update(CREATE_FILM_DIRECTORS, id, director.getId());
        }

        return film;
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
        saveDirectors(film);
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


    public List<Film> getPopularFilms(Integer count, Optional<Long> genreId, Optional<Integer> year) {
        String getPopularFilmsSB = GET_POPULAR_FILMS +
                (genreId.isPresent() ?
                        " JOIN film_genres AS fg ON f.film_id = fg.film_id AND fg.genre_id = ?" :
                        "") +
                (year.isPresent() ?
                        " WHERE f.release_date >= ? AND f.release_date <= ? " :
                        "") +
                " ORDER BY f.rate DESC LIMIT ?";

        ArrayList<Object> args = new ArrayList<Object>();
        genreId.ifPresent(args::add);
        if (year.isPresent()) {
            args.add(LocalDate.of(year.get(), 1, 1).toString());
            args.add(LocalDate.of(year.get(), 12, 31).toString());
        }
        args.add(count);
        return jdbc.query(getPopularFilmsSB, mapper, args.toArray());
    }

    private void saveDirectors(NewFilmRequest film) {
        final Long filmId = film.getId();
        jdbc.update("DELETE FROM directed_by WHERE film_id = ?", filmId);
        final List<DirectorDto> directors = film.getDirectors();
        if (directors == null || directors.isEmpty()) {
            return;
        }
        final ArrayList<DirectorDto> directorsList = new ArrayList<>(directors);
        jdbc.batchUpdate(CREATE_FILM_DIRECTORS, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, directorsList.get(i).getId());
                ps.setLong(2, filmId);
            }

            public int getBatchSize() {
                return directorsList.size();
            }
        });
    }

    public List<Film> getDirectorsFilms(long directorId, DirectorQueryParams param) {
        switch (param) {
            case year -> {
                return findMany(FIND_DIRECTORS_FILMS_BY_YEAR, directorId);
            }
            case likes -> {
                return findMany(FIND_DIRECTORS_FILMS_BY_LIKES, directorId);
            }
            default -> {
                return null;
            }
        }

    }
}

