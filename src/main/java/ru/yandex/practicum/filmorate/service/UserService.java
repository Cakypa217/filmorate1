package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.FriendsRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;
    private final EventRepository eventRepository;

    @Autowired
    public UserService(UserRepository userRepository, FriendsRepository friendsRepository,
                       EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
        this.eventRepository = eventRepository;
    }

    public User createUser(User user) {
        validateUser(user);
        User createdUser = userRepository.create(user);
        log.info("Добавлен новый пользователь: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserById(user.getId());
        userRepository.update(user);
        User updatedUser = getUserById(user.getId());
        log.info("Обновлен пользователь: {}", updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long userId) {
        int deletedRows = userRepository.delete(userId);
        if (deletedRows == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        log.info("Удален пользователь с id: {}", userId);
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Получен список всех пользователей. Количество: {}", users.size());
        return users;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        friendsRepository.addFriend(userId, friendId);
        eventRepository.save(new Event(Instant.now().toEpochMilli(), userId,
                "FRIEND", "ADD", friendId));
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        friendsRepository.deleteFriend(userId, friendId);
        eventRepository.save(new Event(Instant.now().toEpochMilli(), userId,
                "FRIEND", "REMOVE", friendId));
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<User> friends = friendsRepository.getFriends(userId);
        log.info("Получен список друзей пользователя {}. Количество: {}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        List<User> commonFriends = friendsRepository.getCommonFriends(userId, otherUserId);
        log.info("Получен список общих друзей пользователей {} и {}. Количество: {}",
                userId, otherUserId, commonFriends.size());
        return commonFriends;
    }

    public List<Event> getUserEvents(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return eventRepository.getUserEvents(userId);
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