package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GenreRepository.class, GenreService.class, GenreRowMapper.class})
public class GenreApplicationTests {

    private final GenreService genreService;

    @Autowired
    public GenreApplicationTests(GenreService genreService) {
        this.genreService = genreService;
    }

    @Test
    public void testGetAllGenres() {
        List<GenreDto> genres = genreService.getAllGenres();

        assertThat(genres).isNotNull();
        assertThat(genres).isNotEmpty();
        assertThat(genres.get(0).getName()).isNotNull();
    }

    @Test
    public void testGetGenreById() {
        Long genreId = 1L;
        GenreDto genre = genreService.getGenreById(genreId);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(genreId);
        assertThat(genre.getName()).isNotNull();
    }
}
