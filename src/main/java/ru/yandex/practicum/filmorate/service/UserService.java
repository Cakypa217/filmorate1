package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        validateUser(user);
        User createdUser = userRepository.create(user);
        log.info("Добавлен новый пользователь: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        log.info("Получен запрос на обновление пользователя: {}", user);
        validateUser(user);
        getUserById(user.getId());
        userRepository.update(user);
        User updatedUser = getUserById(user.getId());
        log.info("Обновлен пользователь: {}", updatedUser);
        return updatedUser;
    }

    public List<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        List<User> users = userRepository.findAll();
        log.info("Получен список всех пользователей. Количество: {}", users.size());
        return users;
    }

    public User getUserById(Long id) {
        log.info("Получен запрос на получение пользователя с id: {}", id);
        log.info("Отправлен ответ с userRepository.findById(id): {}", userRepository.findById(id));
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Получен запрос на добавление пользователя {} в друзья пользователя {}", userId, friendId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        userRepository.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Получен запрос на удаление пользователя {} из друзей пользователя {}", userId, friendId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        userRepository.deleteFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        log.info("Получен запрос на получение списка друзей пользователя {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<User> friends = userRepository.getFriends(userId);
        log.info("Получен список друзей пользователя {}. Количество: {}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Получен запрос на получение списка общих друзей пользователей {} и {}", userId, otherUserId);
        List<User> commonFriends = userRepository.getCommonFriends(userId, otherUserId);
        log.info("Получен список общих друзей пользователей {} и {}. Количество: {}",
                userId, otherUserId, commonFriends.size());
        return commonFriends;
    }

    private void validateUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}