package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_FILMS = "SELECT * FROM films";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    private static final String CREATE_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String CREATE_FILM_MPA = "INSERT INTO film_mpa (film_id, mpa_id) VALUES (?, ?)";
    private static final String CREATE_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET " +
            "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_FILMS = "SELECT f.* FROM films f LEFT " +
            "JOIN likes l ON f.film_id = l.film_id GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

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
        jdbc.update(CREATE_FILM_MPA, id, film.getMpa().getId());
        Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
        for (Genre genre : uniqueGenres) {
            jdbc.update(CREATE_FILM_GENRES, id, genre.getId());
        }
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return jdbc.query(GET_POPULAR_FILMS, mapper, count);
    }
}

