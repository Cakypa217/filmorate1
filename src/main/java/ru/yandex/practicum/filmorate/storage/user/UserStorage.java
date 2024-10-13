package ru.yandex.practicum.filmorate.storage.user;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User add(User user);

    User update(User user);

    List<User> getAll();

    User getById(Long id);

    void delete(Long id);
}
