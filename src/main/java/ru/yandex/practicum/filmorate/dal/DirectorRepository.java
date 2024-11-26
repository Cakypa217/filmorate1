package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_QUERY = "SELECT director_id, name FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";

    public DirectorRepository(JdbcTemplate jdbcTemplate, DirectorRowMapper mapper) {
        super(jdbcTemplate, mapper, Director.class);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> getById(Long directorId) {
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    public boolean delete(Long directorId) {
        return delete(DELETE_QUERY, directorId);
    }

    public Director create(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    public void load(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, film -> film));

        String sqlQuery = "SELECT d.director_id, d.name, db.film_id FROM directors d " +
                "JOIN directed_by db ON d.director_id = db.director_id " +
                "WHERE db.film_id IN (" + inSql + ")";

        jdbc.query(sqlQuery, (ResultSet rs) -> {
            Film film = filmById.get(rs.getLong("film_id"));
            if (film != null) {
                film.getDirectors().add(makeDirector(rs));
            }
        }, films.stream().map(Film::getId).toArray());
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = Director.builder().build();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
