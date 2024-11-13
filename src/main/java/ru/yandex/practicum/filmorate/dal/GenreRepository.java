package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_GENRES_FOR_FILM = "SELECT g.* FROM genres g " +
            "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = ?";
    private static final String UPDATE_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE = "DELETE FROM film_genres WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_GENRES, mapper);
    }

    public Optional<Genre> getGenreById(Long id) {
        return findOne(FIND_GENRE_BY_ID, id);
    }

    public List<Genre> getGenresForFilm(Long filmId) {
        return jdbc.query(FIND_GENRES_FOR_FILM, mapper, filmId);
    }

    public void update(long filmId, List<Long> genreIds) {
        jdbc.update(DELETE, filmId);
        for (Long genreId : genreIds) {
            jdbc.update(UPDATE_GENRE, filmId, genreId);
        }
    }
}
