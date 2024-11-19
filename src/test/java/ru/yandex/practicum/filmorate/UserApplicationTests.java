package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepository.class, UserService.class, UserRowMapper.class})
public class UserApplicationTests {

    private final UserService userService;

    @Autowired
    public UserApplicationTests(UserService userService) {
        this.userService = userService;
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = Optional.ofNullable(userService.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty(); //
        assertThat(users.get(0)).isInstanceOf(User.class);
    }

    @Test
    public void testCreateUser() {
        User user = new User("name", "email@example.com", "login",
                LocalDate.of(2000, 1, 1));
        User createdUser = userService.createUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull(); // Assuming ID is generated
        assertThat(createdUser.getEmail()).isEqualTo("email@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("login");
        assertThat(createdUser.getName()).isEqualTo("name");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    public void testUpdateUser() {
        User user = new User(1L, "updatedEmail@example.com", "updatedLogin", "updatedName",
                LocalDate.of(1990, 1, 1));
        User updatedUser = userService.updateUser(user);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getEmail()).isEqualTo("updatedEmail@example.com");
        assertThat(updatedUser.getLogin()).isEqualTo("updatedLogin");
        assertThat(updatedUser.getName()).isEqualTo("updatedName");
        assertThat(updatedUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testAddFriend() {
        List<User> existingFriends = userService.getFriends(1L);
        boolean alreadyFriends = existingFriends.stream().anyMatch(friend -> friend.getId().equals(2L));
        if (!alreadyFriends) {
            userService.addFriend(1L, 2L);
        }

        List<User> friends = userService.getFriends(1L);
        assertThat(friends).isNotNull();
        assertThat(friends).isNotEmpty();
        assertThat(friends).anyMatch(friend -> friend.getId().equals(2L));
    }

    @Test
    public void testRemoveFriend() {
        List<User> existingFriends = userService.getFriends(1L);
        boolean alreadyFriends = existingFriends.stream().anyMatch(friend -> friend.getId().equals(2L));
        if (!alreadyFriends) {
            userService.addFriend(1L, 2L);
        }
        userService.removeFriend(1L, 2L);

        List<User> friends = userService.getFriends(1L);
        assertThat(friends).isNotNull();
        assertThat(friends).noneMatch(friend -> friend.getId().equals(2L));
    }

    @Test
    public void testGetFriends() {
        List<User> existingFriends = userService.getFriends(1L);
        boolean alreadyFriends = existingFriends.stream().anyMatch(friend -> friend.getId().equals(2L));
        if (!alreadyFriends) {
            userService.addFriend(1L, 2L);
        }

        List<User> friends = userService.getFriends(1L);
        assertThat(friends).isNotNull();
        assertThat(friends).isNotEmpty();
        assertThat(friends).anyMatch(friend -> friend.getId().equals(2L));
    }

}