package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void load(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, film -> film));

        String sqlQuery = "SELECT g.genre_id, g.name, fg.film_id FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id IN (" + inSql + ")";

        jdbc.query(sqlQuery, (ResultSet rs) -> {
            Film film = filmById.get(rs.getLong("film_id"));
            if (film != null) {
                film.getGenres().add(makeGenre(rs));
            }
        }, films.stream().map(Film::getId).toArray());
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
