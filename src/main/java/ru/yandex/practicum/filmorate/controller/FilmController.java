package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос GET /films");
        Collection<Film> films = filmService.getAllFilms();
        log.info("Отправлен ответ GET /films с количеством фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос POST /films с телом: {}", film);
        Film createdFilm = filmService.addFilm(film);
        log.info("Отправлен ответ POST /films с телом: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос PUT /films с телом: {}", film);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Отправлен ответ PUT /films с телом: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен запрос GET /films/{}", id);
        Film film = filmService.getFilmById(id);
        log.info("Отправлен ответ GET /films/{} с телом: {}", id, film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк успешно добавлен для фильма с id {} от пользователя с id {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос DELETE /films/{}/like/{}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Лайк успешно удален для фильма с id {} от пользователя с id {}", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос GET /films/popular?count={}", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Отправлен ответ GET /films/popular с количеством фильмов: {}", popularFilms.size());
        return popularFilms;
    }
}
