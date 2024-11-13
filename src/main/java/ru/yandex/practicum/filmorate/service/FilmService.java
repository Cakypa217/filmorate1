package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.time.LocalDate;
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

    @Autowired
    public FilmService(FilmRepository filmRepository, GenreRepository genreRepository,
                       MpaRepository mpaRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.userRepository = userRepository;
    }

    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        log.info("Получен запрос createFilm с newFilmRequest: {}", newFilmRequest);
        Mpa mpa = mpaRepository.getMpaById(newFilmRequest.getMpa().getId());
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
        log.info("Получен запрос на получение всех фильмов");
        log.info("Найдены фильмы: {}", filmRepository.findAll());
        return filmRepository.findAll();
    }


    public FilmDto getFilmById(Long filmId) {
        log.info("Получен запрос на получение фильма с id: {}", filmId);
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Long mpaId = mpaRepository.getMpaForFilm(filmId);
        Mpa mpa = mpaRepository.getMpaById(mpaId);
        film.setMpa(mpa);
        film.setMpa(mpa);
        List<Genre> genres = genreRepository.getGenresForFilm(filmId);
        film.setGenres(genres);
        log.info("Отправлен ответ с FilmMapper.mapToFilmDto(film): {}", FilmMapper.mapToFilmDto(film));
        return FilmMapper.mapToFilmDto(film);
    }


    public FilmDto updateFilm(NewFilmRequest newFilmRequest) {
        log.info("Получен запрос на обновление фильма newFilmRequest: {}", newFilmRequest);
        Film film = filmRepository.findById(newFilmRequest.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        Mpa mpa = mpaRepository.getMpaById(newFilmRequest.getMpa().getId());
        mpaRepository.update(film.getId(), mpa.getId());

        List<Long> genreIds = newFilmRequest.getGenres().stream()
                .map(GenreDto::getId)
                .collect(Collectors.toList());
        genreRepository.update(film.getId(), genreIds);

        List<Genre> genres = genreRepository.getGenresForFilm(film.getId());
        Film updatedFilm = FilmMapper.updateFilm(film, newFilmRequest, mpa, genres);
        filmRepository.update(updatedFilm);
        log.info("Отправлен ответ с FilmMapper.mapToFilmDto(updatedFilm): {}", FilmMapper.mapToFilmDto(updatedFilm));
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получен запрос на получение списка популярных фильмов. Количество: {}", count);
        List<Film> popularFilms = filmRepository.getPopularFilms(count);
        log.info("Получен список популярных фильмов. Количество: {}", popularFilms.size());
        return popularFilms;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Получен запрос на добавление лайка фильму {} от пользователя {}", filmId, userId);
        checkFilmAndUserExist(filmId, userId);
        filmRepository.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Получен запрос на удаление лайка у фильма {} от пользователя {}", filmId, userId);
        checkFilmAndUserExist(filmId, userId);
        filmRepository.removeLike(filmId, userId);
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
