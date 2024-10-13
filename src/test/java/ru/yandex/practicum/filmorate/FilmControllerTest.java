package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    void testFilmGetAll() {
        Film film = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        long size = filmController.findAll().size();
        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm.getId());
        size++;
        assertEquals(size, filmController.findAll().size());
        assertTrue(filmController.findAll().contains(createdFilm));
    }

    @Test
    void testValidFilmNameCreation() {
        Film film = new Film("", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testValidFilmDescriptionCreation() {
        Film film = new Film("", "Описание 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16," +
                " 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40," +
                " 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64," +
                " 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88," +
                " 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100",
                LocalDate.of(2000, 1, 1), 120);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testValidFilmReleaseDateCreation() {
        Film film = new Film("Фильм 1", "Описание 1",
                LocalDate.of(1800, 1, 1), 120);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testValidFilmDurationCreation() {
        Film film = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), -120);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testValidFilmNameUpdate() {
        Film film1 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        filmController.create(film1);
        Film film2 = new Film("", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        assertThrows(ValidationException.class, () -> filmController.update(film2));
    }

    @Test
    void testValidFilmDescriptionUpdate() {
        Film film1 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        filmController.create(film1);
        String longDescription = "Описание 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16," +
                " 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36," +
                " 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56," +
                " 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, " +
                "77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100";
        Film film2 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        assertThrows(NotFoundException.class, () -> filmController.update(film2));
    }

    @Test
    void testValidFilmReleaseDateUpdate() {
        Film film1 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        filmController.create(film1);
        Film film2 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        assertThrows(NotFoundException.class, () -> filmController.update(film2));
    }

    @Test
    void testValidFilmDurationUpdate() {
        Film film1 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), 120);
        filmController.create(film1);
        Film film2 = new Film("Фильм 1", "Описание 1",
                LocalDate.of(2000, 1, 1), -120);
        assertThrows(ValidationException.class, () -> filmController.update(film2));
    }
}