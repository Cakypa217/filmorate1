package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, GenreRepository genreRepository,
                       MpaRepository mpaRepository, UserRepository userRepository
            , LikeRepository likeRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        Mpa mpa = mpaRepository.getMpaById(newFilmRequest.getMpa().getId(),
                () -> new MpaNotFoundException("MPA с id " + newFilmRequest.getMpa().getId() + " не найден"));
        List<Genre> genres = newFilmRequest.getGenres().stream()
                .map(genreDto -> genreRepository.getGenreById(genreDto.getId())
                        .orElseThrow(() -> new MpaNotFoundException("Жанр с id " + genreDto.getId() + " не найден")))
                .collect(Collectors.toList());
        Film film = FilmMapper.mapToFilm(newFilmRequest, mpa, genres);
        validateFilm(film);
        filmRepository.create(film);
        log.info("Отправлен ответ с FilmMapper.mapToFilmDto(film): {}", FilmMapper.mapToFilmDto(film));
        return FilmMapper.mapToFilmDto(film);
    }


    public List<Film> getAllFilms() {
        final List<Film> films = filmRepository.findAll();
        genreRepository.load(films);
        films.forEach(film -> {
            Mpa mpa = mpaRepository.getMpaById(film.getMpa().getId(),
                    () -> new MpaNotFoundException("MPA с id " + film.getMpa().getId() + " не найден"));
            film.setMpa(mpa);
        });
        log.info("Найдены фильмы: {}", films);
        return films;
    }


    public FilmDto getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Mpa mpa = mpaRepository.getMpaById(film.getMpa().getId(),
                () -> new MpaNotFoundException("MPA с id " + film.getMpa().getId() + " не найден"));
        film.setMpa(mpa);
        genreRepository.load(Collections.singletonList(film));
        log.info("Отправлен ответ с FilmMapper.mapToFilmDto(film): {}", FilmMapper.mapToFilmDto(film));
        return FilmMapper.mapToFilmDto(film);
    }


    public NewFilmRequest update(NewFilmRequest newFilmRequest) {
        Film film = filmRepository.findById(newFilmRequest.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        film.setRate(newFilmRequest.getRate());
        filmRepository.update(newFilmRequest);
        log.info("Отправлен ответ : {}", newFilmRequest);
        return newFilmRequest;
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmRepository.getPopularFilms(count);
        genreRepository.load(popularFilms);
        popularFilms.forEach(film -> {
            Mpa mpa = mpaRepository.getMpaById(film.getMpa().getId(),
                    () -> new MpaNotFoundException("MPA с id " + film.getMpa().getId() + " не найден"));
            film.setMpa(mpa);
        });
        log.info("Получен список популярных фильмов. Количество: {}", popularFilms.size());
        return popularFilms;
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmAndUserExist(filmId, userId);
        likeRepository.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        checkFilmAndUserExist(filmId, userId);
        likeRepository.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
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
