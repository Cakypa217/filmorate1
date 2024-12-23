package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос GET /users");
        List<User> users = userService.getAllUsers();
        log.info("Отправлен ответ GET /users. Всего {} пользователей : {}", users.size(), users);
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос POST /users с телом: {}", user);
        User createdUser = userService.createUser(user);
        log.info("Отправлен ответ POST /users с телом: {}", createdUser);
        return createdUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
        log.info("Отправлен ответ DELETE /users/{}", userId);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT /users с телом: {}", user);
        User updatedUser = userService.updateUser(user);
        log.info("Отправлен ответ PUT /users с телом: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}", id);
        User user = userService.getUserById(id);
        log.info("Отправлен ответ GET /users/{} с телом: {}", id, user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос PUT /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос DELETE /users/{}/friends/{}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}/friends", id);
        List<User> friends = userService.getFriends(id);
        log.info("Отправлен ответ GET /users/{}/friends. Всего {} друзей: {}", id, friends.size(),
                friends);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос GET /users/{}/friends/common/{}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Отправлен ответ GET /users/{}/friends/common/{}. Всего {} общих друзей: {}", id,
                otherId, commonFriends.size(), commonFriends);
        return commonFriends;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}/feed", id);
        List<Event> events = userService.getUserEvents(id);
        log.info("Отправлен ответ GET /users/{}/feed. Всего {} событий: {}", id, events.size(), events);
        return events;
    }
    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}/recommendations", id);
        final List<Film> recommendedFilms = filmService.getRecommendations(id);
        log.info("Отправлен ответ GET /users/{}/recommendations. Всего {} рекомендаций: {}",
                id, recommendedFilms.size(), recommendedFilms);
        return recommendedFilms;
    }
}