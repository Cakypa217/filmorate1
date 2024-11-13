package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MpaRepository.class, MpaService.class, MpaRowMapper.class})
public class MpaApplicationTests {

    private final MpaService mpaService;

    @Autowired
    public MpaApplicationTests(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @Test
    public void testGetAllMpa() {
        List<MpaDto> mpaList = mpaService.getAllMpa();

        assertThat(mpaList).isNotNull();
        assertThat(mpaList).isNotEmpty();
        assertThat(mpaList.get(0).getName()).isNotNull();
    }

    @Test
    public void testGetMpaById() {
        Long mpaId = 1L;
        MpaDto mpa = mpaService.getMpaById(mpaId);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(mpaId);
        assertThat(mpa.getName()).isNotNull();
    }
}
