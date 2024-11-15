package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        log.info("Получены жанры: {}", genres);
        return genres.stream()
                .map(GenreMapper::toGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(Long id) {
        Genre genre = genreRepository.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
        log.info("ОТправлен ответ: {}", GenreMapper.toGenreDto(genre));
        return GenreMapper.toGenreDto(genre);
    }
}
