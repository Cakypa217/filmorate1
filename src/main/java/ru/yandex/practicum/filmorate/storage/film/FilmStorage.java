package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getById(Long id);

    void delete(Long id);
}
