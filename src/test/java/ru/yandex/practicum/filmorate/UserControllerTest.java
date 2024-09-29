package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void testValidUserEmail() {
        User user = new User("name", "user@email.com", "login",
                LocalDate.of(1990, 1, 1));
        assertDoesNotThrow(() -> userController.create(user));
    }


    @Test
    void testValidUserBirthday() {
        User user = new User("name", "user@email.com", "login",
                LocalDate.of(1990, 1, 1));
        assertDoesNotThrow(() -> userController.create(user));
    }

    @Test
    void testInvalidUserBirthday() {
        User user = new User("name", "user@email.com", "login",
                LocalDate.of(2025, 1, 1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }


    @Test
    void testValidUserCreation() {
        User user = new User("name", "user@email.com", "login",
                LocalDate.of(1990, 1, 1));
        User createdUser = userController.create(user);
        assertNotNull(createdUser.getId());
    }

    @Test
    void testInvalidUserEmail() {
        User user = new User("name", "invalid-email", "login",
                LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }
}