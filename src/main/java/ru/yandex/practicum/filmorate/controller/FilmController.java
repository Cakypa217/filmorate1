package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        log.info("Получен запрос на создание фильма: {}", newFilm);
        validateFilm(newFilm);
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Добавлен фильм с идентификатором {}", newFilm.getId());
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Фильм с идентификатором {} не найден", newFilm.getId());
            throw new ValidationException("Фильм не найден");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с идентификатором {} обновлен.", newFilm.getId());
        return newFilm;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации: описание фильма не может превышать 200 символов.");
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.error("Ошибка валидации: дата выпуска фильма не может быть ранее 28 декабря 1895 г.");
            throw new ValidationException("Дата выпуска не может быть ранее 28 декабря 1895 г.");
        }
        if (film.getDuration().getSeconds() <= 0) {
            log.error("Ошибка валидации: продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}