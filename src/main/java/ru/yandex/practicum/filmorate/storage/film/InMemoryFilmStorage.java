package ru.yandex.practicum.filmorate.storage.film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long generatorId = 0;

    @Override
    public Film add(Film film) {
        film.setId(++generatorId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Long id) {
        return films.get(id);
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
    }
}
