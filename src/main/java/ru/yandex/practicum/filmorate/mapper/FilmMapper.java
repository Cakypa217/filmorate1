package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest newFilmRequest, Mpa mpa, List<Genre> genres, List<Director> directors) {
        Film film = new Film();
        film.setName(newFilmRequest.getName());
        film.setDescription(newFilmRequest.getDescription());
        film.setReleaseDate(newFilmRequest.getReleaseDate());
        film.setDuration(newFilmRequest.getDuration());
        film.setRate(newFilmRequest.getRate());
        film.setMpa(mpa);
        film.setGenres(genres);
        film.setDirectors(directors);
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());

        Mpa mpa = film.getMpa();
        filmDto.setMpa(MpaMapper.mapToMpaDto(mpa));

        if (Objects.nonNull(film.getGenres())) {
            List<GenreDto> genreDtos = film.getGenres()
                    .stream()
                    .map(GenreMapper::toGenreDto)
                    .collect(Collectors.toList());
            filmDto.setGenres(genreDtos);
        }

        if (Objects.nonNull(film.getDirectors())) {
            List<DirectorDto> directorDtos = film.getDirectors()
                    .stream()
                    .map(DirectorMapper::mapToDirectorDto)
                    .toList();
            filmDto.setDirectors(directorDtos);
        }

        return filmDto;
    }

    public static Film updateFilm(Film film, NewFilmRequest newFilmRequest, Mpa mpa, List<Genre> genres, List<Director> directors) {
        if (newFilmRequest.getName() != null) {
            film.setName(newFilmRequest.getName());
        }
        if (newFilmRequest.getDescription() != null) {
            film.setDescription(newFilmRequest.getDescription());
        }
        if (newFilmRequest.getReleaseDate() != null) {
            film.setReleaseDate(newFilmRequest.getReleaseDate());
        }
        if (newFilmRequest.getDuration() != null) {
            film.setDuration(newFilmRequest.getDuration());
        }
        if (newFilmRequest.getRate() != null) {
            film.setRate(newFilmRequest.getRate());
        }
        if (mpa != null) {
            film.setMpa(mpa);
        }
        if (genres != null) {
            film.setGenres(genres);
        }
        if (directors != null) {
            film.setDirectors(directors);
        }
        return film;
    }
}
