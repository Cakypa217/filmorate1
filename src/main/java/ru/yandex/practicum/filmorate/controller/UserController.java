package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    protected long generatorId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел GET запрос /users");
        Collection<User> response = users.values();
        log.info("Отправлен ответ GET /users с телом: {}", response);
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Пришел POST запрос /users с телом: {}", user);
        validateUser(user);
        long id = generatorId++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с идентификатором {}", user.getId());
        log.info("Отправлен ответ POST /users с телом: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Пришел PUT запрос /users с телом: {}", newUser);
        validateUser(newUser);
        if (!users.containsKey(newUser.getId())) {
            log.warn("Пользователь с идентификатором {} не найден", newUser.getId());
            throw new ValidationException("Пользователь с ID " + newUser.getId() + " не найден");
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с идентификатором {} обновлен.", newUser.getId());
        log.info("Отправлен ответ PUT /users с телом: {}", newUser);
        return newUser;
    }

    private void validateUser(@Valid User user) {
        if (!user.getEmail().contains("@")) {
            log.error("Ошибка валидации: email не может быть пустым или не содержать символа '@'");
            throw new ValidationException("Email не может быть пустым или не содержать символа '@'");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: логин не может быть пустым или содержать пробелы");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName().isBlank()) {
            log.info("Ошибка валидации: некорректный логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}