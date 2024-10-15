package ru.yandex.practicum.filmorate.storage.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long generatorId = 0;

    @Override
    public User add(User user) {
        user.setId(++generatorId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
