package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FilmRepository.class, UserRepository.class, GenreRepository.class, MpaRepository.class,
        FilmService.class, UserService.class, GenreService.class, MpaService.class,
        FilmRowMapper.class, UserRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
public class FilmApplicationTests {

    private final FilmService filmService;

    @Autowired
    public FilmApplicationTests(FilmService filmService) {
        this.filmService = filmService;
    }

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmService.getAllFilms();
        assertThat(films).isNotNull();
        assertThat(films).isNotEmpty();
        assertThat(films.get(0).getName()).isNotNull();
    }

    @Test
    public void testAddFilm() {
        NewFilmRequest newFilmRequest = new NewFilmRequest();
        newFilmRequest.setName("New Film");
        newFilmRequest.setDescription("A new film description");
        newFilmRequest.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilmRequest.setDuration(120L);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1L);
        mpaDto.setName("G");
        newFilmRequest.setMpa(mpaDto);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("Action");
        newFilmRequest.setGenres(List.of(genreDto));

        FilmDto addedFilm = filmService.createFilm(newFilmRequest);
        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isNotNull();
        assertThat(addedFilm.getName()).isEqualTo("New Film");
        assertThat(addedFilm.getDescription()).isEqualTo("A new film description");
        assertThat(addedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(addedFilm.getDuration()).isEqualTo(120L);
        assertThat(addedFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(addedFilm.getGenres()).hasSize(1);
        assertThat(addedFilm.getGenres().get(0).getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateFilm() {
        NewFilmRequest newFilmRequest = new NewFilmRequest();
        newFilmRequest.setName("Original Film");
        newFilmRequest.setDescription("Original description");
        newFilmRequest.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilmRequest.setDuration(120L);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1L);
        mpaDto.setName("G");
        newFilmRequest.setMpa(mpaDto);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("Action");
        newFilmRequest.setGenres(List.of(genreDto));

        FilmDto addedFilm = filmService.createFilm(newFilmRequest);

        NewFilmRequest updateFilmRequest = new NewFilmRequest();
        updateFilmRequest.setId(addedFilm.getId());
        updateFilmRequest.setName("Updated Film");
        updateFilmRequest.setDescription("Updated description");
        updateFilmRequest.setReleaseDate(LocalDate.of(2023, 2, 1));
        updateFilmRequest.setDuration(150L);

        MpaDto updatedMpaDto = new MpaDto();
        updatedMpaDto.setId(2L);
        updatedMpaDto.setName("PG");
        updateFilmRequest.setMpa(updatedMpaDto);

        GenreDto updatedGenreDto = new GenreDto();
        updatedGenreDto.setId(2L);
        updatedGenreDto.setName("Comedy");
        updateFilmRequest.setGenres(List.of(updatedGenreDto));

        NewFilmRequest updatedFilm = filmService.update(updateFilmRequest);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getId()).isEqualTo(addedFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2023, 2, 1));
        assertThat(updatedFilm.getDuration()).isEqualTo(150L);
        assertThat(updatedFilm.getMpa().getId()).isEqualTo(2L);
        assertThat(updatedFilm.getGenres()).hasSize(1);
        assertThat(updatedFilm.getGenres().get(0).getId()).isEqualTo(2L);
    }

    @Test
    public void testGetFilmById() {
        NewFilmRequest newFilmRequest = new NewFilmRequest();
        newFilmRequest.setName("Test Film");
        newFilmRequest.setDescription("Test film description");
        newFilmRequest.setReleaseDate(LocalDate.of(2023, 3, 1));
        newFilmRequest.setDuration(100L);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1L);
        mpaDto.setName("G");
        newFilmRequest.setMpa(mpaDto);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("Action");
        newFilmRequest.setGenres(List.of(genreDto));

        FilmDto addedFilm = filmService.createFilm(newFilmRequest);

        FilmDto retrievedFilm = filmService.getFilmById(addedFilm.getId());

        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getId()).isEqualTo(addedFilm.getId());
        assertThat(retrievedFilm.getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.getDescription()).isEqualTo("Test film description");
        assertThat(retrievedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2023, 3, 1));
        assertThat(retrievedFilm.getDuration()).isEqualTo(100L);
        assertThat(retrievedFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(retrievedFilm.getGenres()).hasSize(1);
        assertThat(retrievedFilm.getGenres().get(0).getId()).isEqualTo(1L);
    }
}
