package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.DirectorQueryParams;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;
    private final DirectorRepository directorRepository;

    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        Mpa mpa = mpaRepository.getMpaById(newFilmRequest.getMpa().getId(),
                () -> new MpaNotFoundException("MPA с id " + newFilmRequest.getMpa().getId() + " не найден"));
        List<Genre> genres = newFilmRequest.getGenres().stream()
                .map(genreDto -> genreRepository.getGenreById(genreDto.getId())
                        .orElseThrow(() -> new MpaNotFoundException("Жанр с id " + genreDto.getId() + " не найден")))
                .collect(Collectors.toList());
        List<Director> directors = newFilmRequest.getDirectors().stream()
                .map(directorDto -> directorRepository.getById(directorDto.getId())
                        .orElseThrow(() -> new NotFoundException("Режиссер с id " + directorDto.getId() + " не найден")))
                .collect(Collectors.toList());
        Film film = FilmMapper.mapToFilm(newFilmRequest, mpa, genres, directors);
        validateFilm(film);
        filmRepository.create(film);
        log.info("Отправлен ответ с FilmMapper.mapToFilmDto(film): {}", FilmMapper.mapToFilmDto(film));
        return FilmMapper.mapToFilmDto(film);
    }

    public void deleteFilm(Long id) {
        int rowsAffected = filmRepository.deleteFilm(id);
        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        log.info("Фильм с id {} удален", id);
    }

    public List<Film> getAllFilms() {
        final List<Film> films = filmRepository.findAll();
        genreRepository.load(films);
        directorRepository.load(films);
        log.info("Найдены фильмы: {}", films);
        return films;
    }

    public FilmDto getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        genreRepository.load(Collections.singletonList(film));
        directorRepository.load(Collections.singletonList(film));
        log.info("Отправлен ответ с FilmMapper.mapToFilmDto(film): {}", FilmMapper.mapToFilmDto(film));
        return FilmMapper.mapToFilmDto(film);
    }

    public List<Film> getFilmsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Film> films = filmRepository.findByIds(ids);
        genreRepository.load(films);
        directorRepository.load(films);
        log.info("Найдены фильмы по заданному списку id: {}", films);
        return films;
    }

    public NewFilmRequest update(NewFilmRequest newFilmRequest) {
        Film film = filmRepository.findById(newFilmRequest.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        film.setRate(newFilmRequest.getRate());
        filmRepository.update(newFilmRequest);
        log.info("Отправлен ответ : {}", newFilmRequest);
        return newFilmRequest;
    }

    public List<Film> getPopularFilms(Integer count, Optional<Long> genreId, Optional<Integer> year) {
        List<Film> popularFilms = filmRepository.getPopularFilms(count, genreId, year);
        genreRepository.load(popularFilms);
        directorRepository.load(popularFilms);
        log.info("Получен список популярных фильмов. Количество: {}", popularFilms.size());
        return popularFilms;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> commonFilms = filmRepository.getCommonFilms(userId, friendId);
        genreRepository.load(commonFilms);
        directorRepository.load(commonFilms);
        log.info("Получен список общих фильмов. Количество: {}", commonFilms.size());
        return commonFilms;
    }

    public List<Film> getDirectorsFilms(Long directorId, String sortBy) {
        try {
            List<Film> directorsFilms = filmRepository.getDirectorsFilms(directorId, DirectorQueryParams.valueOf(sortBy));
            if (directorsFilms.isEmpty()) {
                throw new NotFoundException("Фильмы по режиссёру не найдены");
            }
            genreRepository.load(directorsFilms);
            directorRepository.load(directorsFilms);
            log.info("Получен список фильмов режиссера {}", directorId);
            return directorsFilms;
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Некорректный параметр запроса " + sortBy);
        }
    }

    public List<Film> searchFilmBy(String query, String by) {
        List<Film> searchFilms = filmRepository.searchFilmBy(query, by);
        genreRepository.load(searchFilms);
        directorRepository.load(searchFilms);
        return searchFilms;
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmAndUserExist(filmId, userId);
        likeRepository.addLike(filmId, userId);
        eventRepository.save(new Event(Instant.now().toEpochMilli(), userId,
                EventType.LIKE, OperationType.ADD, filmId));
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        checkFilmAndUserExist(filmId, userId);
        likeRepository.removeLike(filmId, userId);
        eventRepository.save(new Event(Instant.now().toEpochMilli(), userId,
                EventType.LIKE, OperationType.REMOVE, filmId));
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getRecommendations(Long userId) {
        return getFilmsByIds(likeRepository.getRecommendedFilmsIds(userId));
    }

    private void checkFilmAndUserExist(Long filmId, Long userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public void validateFilm(Film film) {
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
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: продолжительность фильма должна быть отрицательной.");
            throw new ValidationException("Продолжительность фильма должна быть отрицательной.");
        }
    }
}
