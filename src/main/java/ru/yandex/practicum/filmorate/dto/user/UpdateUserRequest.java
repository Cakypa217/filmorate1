package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    private LocalDate birthday;
}
