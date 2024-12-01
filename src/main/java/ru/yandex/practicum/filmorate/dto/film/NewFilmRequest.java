package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewFilmRequest {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    private Integer rate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;

    private MpaDto mpa;
    private List<GenreDto> genres = new ArrayList<>();
    private List<DirectorDto> directors = new ArrayList<>();
}
