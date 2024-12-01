package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен запрос GET /films");
        List<Film> films = filmService.getAllFilms();
        log.info("Отправлен ответ GET /films. Всего {} фильмов: {}", films.size(), films);
        return films;
    }

    @PostMapping
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest film) {
        log.info("Получен запрос POST /films с телом: {}", film);
        FilmDto addedFilm = filmService.createFilm(film);
        log.info("Отправлен ответ POST /films с телом: {}", addedFilm);
        return addedFilm;
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") Long id) {
        log.info("Получен запрос DELETE /films/{}", id);
        filmService.deleteFilm(id);
        log.info("Отправлен ответ DELETE /films/{}", id);
    }

    @PutMapping
    public NewFilmRequest updateFilm(@Valid @RequestBody NewFilmRequest newFilmRequest) {
        log.info("Получен запрос PUT /films с телом: {}", newFilmRequest);
        NewFilmRequest updatedFilm = filmService.update(newFilmRequest);
        log.info("Отправлен ответ PUT /films с телом: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        log.info("Получен запрос GET /films/{}", id);
        FilmDto film = filmService.getFilmById(id);
        log.info("Отправлен ответ GET /films/{} с телом: {}", id, film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос DELETE /films/{}/like/{}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") Integer count,
                                      @RequestParam(name = "genreId", required = false) Optional<Long> genreId,
                                      @RequestParam(name = "year", required = false) Optional<Integer> year) {
        log.info("Получен запрос GET /films/popular?count={}", count);
        List<Film> popularFilms = filmService.getPopularFilms(count, genreId, year);
        log.info("Отправлен ответ GET /films/popular. Всего {} фильмов: {}", popularFilms.size(),
                popularFilms);
        return popularFilms;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Long userId,
                                     @RequestParam(name = "friendId") Long friendId) {
        log.info("Получен запрос GET /films/common?userId={}&friendId={}", userId, friendId);
        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Отправлен ответ GET /films/common. Всего {} фильмов: {}", commonFilms.size(),
                commonFilms);
        return commonFilms;
    }
  
    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorsFilms(@PathVariable Long directorId,
                                        @RequestParam(name = "sortBy") String sortBy) {
        log.info("Получен запрос GET /films/director/{}?sortBy=[{}]", directorId, sortBy);
        List<Film> directorsFilms = filmService.getDirectorsFilms(directorId, sortBy);
        log.info("Отправлен ответ GET /films/director. Всего {} фильмов: {}", directorsFilms.size(),
                directorsFilms);
        return directorsFilms;
    }

    @GetMapping("/search")
    public List<Film> getSearchFilmsBy(
            @RequestParam String query,
            @RequestParam String by) {
        log.info("Получен запрос GET /films/search?query={}&by={}", query, by);
        final List<Film> searchResult = filmService.searchFilmBy(query, by);
        log.info("Отправлен ответ GET /films/search?query={}&by={}. Всего {} фильмов: {}",
                query, by, searchResult.size(), searchResult);
        return searchResult;
    }
}
