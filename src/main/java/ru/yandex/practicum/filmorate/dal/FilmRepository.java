package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.enums.DirectorQueryParams;
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
    private static final String GET_POPULAR_FILMS =
            "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, " +
            "COUNT(l.film_id) AS cnt " +
            "FROM films AS f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.film_id) DESC LIMIT ?";
    private static final String GET_POPULAR_FILMS_BY_YEAR =
            "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, " +
            "COUNT(l.film_id) AS cnt " +
            "FROM films AS f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "WHERE f.release_date >= ? AND f.release_date <= ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.film_id) DESC LIMIT ?";
    private static final String GET_POPULAR_FILMS_BY_GENRE =
            "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, " +
            "COUNT(l.film_id) AS cnt " +
            "FROM films AS f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "JOIN film_genres AS fg ON f.film_id = fg.film_id AND fg.genre_id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.film_id) DESC LIMIT ?";
    private static final String GET_POPULAR_FILMS_BY_GENRE_AND_YEAR =
            "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, " +
            "COUNT(l.film_id) AS cnt " +
            "FROM films AS f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "JOIN film_genres AS fg ON f.film_id = fg.film_id AND fg.genre_id = ? " +
            "WHERE f.release_date >= ? AND f.release_date <= ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.film_id) DESC LIMIT ?";
    private static final String GET_COMMON_FILMS = "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name" +
            " FROM likes AS l" +
            " JOIN films AS f ON l.film_id = f.film_id" +
            " JOIN mpa m ON f.mpa_id = m.mpa_id" +
            " WHERE l.user_id = ?" +
            " AND l.film_id IN (SELECT fl.film_id FROM likes AS fl WHERE fl.user_id = ?)" +
            " ORDER BY f.rate DESC";
    private static final String CREATE_FILM = "INSERT INTO films (" +
            "name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
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
        Set<Genre> uniqueGenres = film.getGenres() != null ?
                new HashSet<>(film.getGenres()) :
                new HashSet<>();
        for (Genre genre : uniqueGenres) {
            jdbc.update(CREATE_FILM_GENRES, id, genre.getId());
        }

        Set<Director> uniqueDirectors = film.getDirectors() != null ?
                new HashSet<>(film.getDirectors()) :
                new HashSet<>();
        for (Director director : uniqueDirectors) {
            jdbc.update(CREATE_FILM_DIRECTORS, director.getId(), id);
        }

        return film;
    }

    public int deleteFilm(long filmId) {
        return jdbc.update(DELETE_FILM, filmId);
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
        final Set<GenreDto> uniqueGenres = new HashSet<>(film.getGenres());
        jdbc.batchUpdate(CREATE_FILM_GENRES, new BatchPreparedStatementSetter() {
            private final Iterator<GenreDto> iterator = uniqueGenres.iterator();

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                if (iterator.hasNext()) {
                    GenreDto genre = iterator.next();
                    ps.setLong(1, filmId);
                    ps.setLong(2, genre.getId());
                }
            }

            public int getBatchSize() {
                return uniqueGenres.size();
            }
        });
    }

    public List<Film> getPopularFilms(Integer count) {
        return jdbc.query(GET_POPULAR_FILMS, mapper, count);
    }

    public List<Film> getPopularFilmsByYear(Integer count, Integer year) {
        return jdbc.query(GET_POPULAR_FILMS_BY_YEAR, mapper,
                LocalDate.of(year, 1, 1).toString(),
                LocalDate.of(year, 12, 31).toString(),
                count);
    }

    public List<Film> getPopularFilmsByGenre(Integer count, Long genreId) {
        return jdbc.query(GET_POPULAR_FILMS_BY_GENRE, mapper, genreId, count);
    }

    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Long genreId, Integer year) {
        return jdbc.query(GET_POPULAR_FILMS_BY_GENRE_AND_YEAR, mapper,
                genreId,
                LocalDate.of(year, 1, 1).toString(),
                LocalDate.of(year, 12, 31).toString(),
                count);
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

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return jdbc.query(GET_COMMON_FILMS, mapper, userId, friendId);
    }

    public List<Film> searchFilmBy(String query, String by) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f. mpa_id, m.NAME as mpa_name, " +
                "COUNT (L.FILM_ID) AS rate FROM FILMS f " +
                "INNER JOIN mpa M ON F.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN DIRECTED_BY AS db ON f.film_id = db.film_id " +
                "LEFT JOIN directors AS d ON db.director_id = d.DIRECTOR_ID " +
                interpretQuery(query, by) +
                "GROUP BY F.film_id ORDER BY rate DESC";
        return jdbc.query(sql, mapper);
    }

    private String interpretQuery(String query, String by) {
        switch (by) {
            case "title":
                return "WHERE LOWER(f.name) LIKE " + "LOWER('%" + query + "%') ";
            case "director":
                return "WHERE LOWER(d.name) LIKE " + "LOWER('%" + query + "%') ";
            default:
                return "WHERE LOWER(d.name) LIKE " + "LOWER('%" + query + "%') OR " +
                        "LOWER(f.name) LIKE " + "LOWER('%" + query + "%') ";
        }
    }
}

