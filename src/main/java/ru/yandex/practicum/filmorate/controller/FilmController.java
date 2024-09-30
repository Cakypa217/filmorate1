package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    protected long generatorId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел GET запрос /films");
        Collection<Film> response = films.values();
        log.info("Отправлен ответ GET /films с телом: {}", response);
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        log.info("Пришел POST запрос /films с телом: {}", newFilm);
        validateFilm(newFilm);
        long id = generatorId++;
        newFilm.setId(id);
        films.put(newFilm.getId(), newFilm);
        log.info("Добавлен фильм с идентификатором {}", newFilm.getId());
        log.info("Отправлен ответ POST /films с телом: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Пришел PUT запрос /films с телом: {}", newFilm);
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Фильм с идентификатором {} не найден", newFilm.getId());
            throw new ValidationException("Фильм не найден");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с идентификатором {} обновлен.", newFilm.getId());
        log.info("Отправлен ответ PUT /films с телом: {}", newFilm);
        return newFilm;
    }

    private void validateFilm(@Valid Film film) {
        if (film.getName().isBlank()) {
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
}