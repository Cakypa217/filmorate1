package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NonNull;

@Data
public class Film {
    private Long id;
    @NonNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }
}